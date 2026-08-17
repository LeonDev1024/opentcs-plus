package org.opentcs.order.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.order.application.bo.TransportOrderQueryBO;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.service.TransportOrderRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 运输订单应用服务测试。
 */
@Tag("dev")
class TransportOrderApplicationServiceTest {

    @Test
    void shouldRejectGenericUpdateForRuntimeOrder() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setOrderNo("order-1");
        existing.setState("ACTIVE");
        when(repository.getById(1L)).thenReturn(existing);

        TransportOrderApplicationService service = service(repository);

        TransportOrderQueryBO bo = new TransportOrderQueryBO();
        bo.setId(1L);
        bo.setName("new-name");

        assertThrows(IllegalStateException.class, () -> service.updateOrder(bo));
    }

    @Test
    void shouldPreserveRuntimeFieldsWhenUpdatingRawOrder() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setOrderNo("order-1");
        existing.setState("RAW");
        when(repository.getById(1L)).thenReturn(existing);

        TransportOrderApplicationService service = service(repository);

        TransportOrderQueryBO bo = new TransportOrderQueryBO();
        bo.setId(1L);
        bo.setName("new-name");
        bo.setOrderNo("tampered-order-no");
        bo.setState("FINISHED");

        service.updateOrder(bo);

        ArgumentCaptor<TransportOrderEntity> captor = ArgumentCaptor.forClass(TransportOrderEntity.class);
        verify(repository).updateById(captor.capture());
        assertEquals("new-name", captor.getValue().getName());
        assertEquals("order-1", captor.getValue().getOrderNo());
        assertEquals("RAW", captor.getValue().getState());
    }

    @Test
    void shouldImportBatchOrdersAsDraftsOnly() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderApplicationService service = service(repository);

        TransportOrderQueryBO bo = new TransportOrderQueryBO();
        bo.setId(99L);
        bo.setName("draft-order");
        bo.setOrderNo("order-1");
        bo.setState("FINISHED");
        bo.setProcessingVehicle("vehicle-1");
        bo.setFinishedTime(LocalDateTime.now());

        service.batchCreate(List.of(bo));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TransportOrderEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).batchCreateTransportOrder(captor.capture());
        TransportOrderEntity entity = captor.getValue().get(0);
        assertNull(entity.getId());
        assertEquals("draft-order", entity.getName());
        assertNull(entity.getOrderNo());
        assertEquals("RAW", entity.getState());
        assertNull(entity.getProcessingVehicle());
        assertNull(entity.getFinishedTime());
    }

    @Test
    void shouldCreateSingleDraftOrderOnly() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderApplicationService service = service(repository);

        TransportOrderQueryBO bo = new TransportOrderQueryBO();
        bo.setId(99L);
        bo.setName("draft-order");
        bo.setState("FINISHED");
        bo.setProcessingVehicle("vehicle-1");
        bo.setFinishedTime(LocalDateTime.now());

        service.createDraftTransportOrder(bo);

        ArgumentCaptor<TransportOrderEntity> captor = ArgumentCaptor.forClass(TransportOrderEntity.class);
        verify(repository).createTransportOrder(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("draft-order", captor.getValue().getName());
        assertNull(captor.getValue().getOrderNo());
        assertEquals("RAW", captor.getValue().getState());
        assertNull(captor.getValue().getProcessingVehicle());
        assertNull(captor.getValue().getFinishedTime());
    }

    @Test
    void shouldRejectEmptyDraftOrder() {
        TransportOrderApplicationService service = service(mock(TransportOrderRepository.class));

        assertThrows(IllegalArgumentException.class, () -> service.createDraftTransportOrder(null));
    }

    @Test
    void shouldDeleteRawDraftOrderOnly() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setState("RAW");
        when(repository.getById(1L)).thenReturn(existing);

        TransportOrderApplicationService service = service(repository);

        service.deleteDraftTransportOrder(1L);

        verify(repository).removeById(1L);
    }

    @Test
    void shouldRejectDeletingRuntimeOrder() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setState("ACTIVE");
        when(repository.getById(1L)).thenReturn(existing);

        TransportOrderApplicationService service = service(repository);

        assertThrows(IllegalStateException.class, () -> service.deleteDraftTransportOrder(1L));
    }

    @Test
    void shouldSubmitDraftOrderThroughKernelAndBindKernelOrderNo() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderApi transportOrderApi = mock(TransportOrderApi.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setState("RAW");
        existing.setName("draft-order");
        existing.setDestinations("P1,P2");
        existing.setIntendedVehicle("vehicle-1");
        when(repository.getById(1L)).thenReturn(existing);
        when(transportOrderApi.createOrder(org.mockito.ArgumentMatchers.any(OrderSpecDTO.class)))
                .thenReturn("kernel-order-1");

        TransportOrderApplicationService service = service(repository, transportOrderApi);

        service.submitDraftTransportOrder(1L);

        ArgumentCaptor<OrderSpecDTO> specCaptor = ArgumentCaptor.forClass(OrderSpecDTO.class);
        verify(transportOrderApi).createOrder(specCaptor.capture());
        assertEquals("draft-order", specCaptor.getValue().getName());
        assertEquals("P1", specCaptor.getValue().getSourcePointId());
        assertEquals("P2", specCaptor.getValue().getDestPointId());
        assertEquals("vehicle-1", specCaptor.getValue().getIntendedVehicle());

        ArgumentCaptor<TransportOrderEntity> entityCaptor = ArgumentCaptor.forClass(TransportOrderEntity.class);
        verify(repository).updateById(entityCaptor.capture());
        assertEquals("kernel-order-1", entityCaptor.getValue().getOrderNo());
        assertEquals("RAW", entityCaptor.getValue().getState());
        verify(transportOrderApi).activateOrder("kernel-order-1");
    }

    @Test
    void shouldRejectSubmittingDraftAlreadyBoundToKernelOrder() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity existing = new TransportOrderEntity();
        existing.setId(1L);
        existing.setState("RAW");
        existing.setOrderNo("kernel-order-1");
        when(repository.getById(1L)).thenReturn(existing);

        TransportOrderApplicationService service = service(repository);

        assertThrows(IllegalStateException.class, () -> service.submitDraftTransportOrder(1L));
    }

    private TransportOrderApplicationService service(TransportOrderRepository repository) {
        return service(repository, mock(TransportOrderApi.class));
    }

    private TransportOrderApplicationService service(TransportOrderRepository repository,
                                                     TransportOrderApi transportOrderApi) {
        return new TransportOrderApplicationService(
                repository,
                mock(TransportOrderRegistry.class),
                mock(VehicleRegistry.class),
                transportOrderApi
        );
    }
}
