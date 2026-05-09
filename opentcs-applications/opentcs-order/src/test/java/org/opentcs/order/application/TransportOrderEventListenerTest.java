package org.opentcs.order.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.event.OrderStateChangedEvent;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.service.TransportOrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单事件监听器测试。
 */
@Tag("dev")
class TransportOrderEventListenerTest {

    @Test
    void shouldUpdatePersistedOrderOnStateChangedEvent() {
        TransportOrderRepository repository = mock(TransportOrderRepository.class);
        TransportOrderEntity entity = new TransportOrderEntity();
        entity.setOrderNo("order-1");
        entity.setState("ACTIVE");
        when(repository.getByOrderNo("order-1")).thenReturn(entity);

        TransportOrderEventListener listener = new TransportOrderEventListener(repository);
        listener.onOrderStateChanged(new OrderStateChangedEvent(
                "order-1",
                OrderState.ACTIVE,
                OrderState.FINISHED,
                "vehicle-1",
                null));

        assertEquals("FINISHED", entity.getState());
        assertEquals("vehicle-1", entity.getProcessingVehicle());
        assertNotNull(entity.getFinishedTime());
        verify(repository).updateById(entity);
    }
}
