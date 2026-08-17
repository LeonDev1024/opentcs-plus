package org.opentcs.kernel.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.application.dispatch.RouteCostDispatchStrategy;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * I4: 3 车 × 20 单并发调度，验证不重复分配同一车辆。
 */
@Tag("dev")
class ConcurrentDispatchStressTest {

    @Test
    void shouldNotAssignSameVehicleToMultipleOrdersConcurrently() throws Exception {
        VehicleRegistry vehicleRegistry = mock(VehicleRegistry.class);
        TransportOrderRegistry orderRegistry = mock(TransportOrderRegistry.class);
        RoutePlannerImpl routePlanner = mock(RoutePlannerImpl.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

        List<Vehicle> vehicles = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> {
                    Vehicle v = new Vehicle("v" + i);
                    v.setName("v" + i);
                    v.updateState(VehicleState.IDLE);
                    v.updatePosition(new VehiclePosition("P" + i, null, i * 10.0, 0, 0, 0));
                    return v;
                })
                .toList();

        // 可用车辆动态：仅返回尚未持有订单的车
        when(vehicleRegistry.getAvailableVehicleDomains()).thenAnswer(inv ->
                vehicles.stream()
                        .filter(v -> v.getCurrentOrderId() == null)
                        .filter(v -> v.getState() == VehicleState.IDLE || v.getState() == VehicleState.CHARGING)
                        .toList());

        when(routePlanner.findRouteDomain(anyString(), anyString(), anyString())).thenAnswer(inv -> {
            String from = inv.getArgument(0);
            String to = inv.getArgument(1);
            return List.of(new Point(from, from, 0, 0), new Point(to, to, 1, 0));
        });
        when(routePlanner.findPath(anyString(), anyString(), anyString())).thenAnswer(inv -> {
            String from = inv.getArgument(0);
            String to = inv.getArgument(1);
            return List.of(new Path(from + "-" + to, from, to, 10));
        });

        DispatcherService dispatcher = new DispatcherService(
                vehicleRegistry,
                orderRegistry,
                routePlanner,
                eventPublisher,
                new InMemoryRuntimeStateStore(),
                new RouteCostDispatchStrategy()
        );
        dispatcher.initialize();

        List<TransportOrder> orders = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> new TransportOrder(
                        "o" + i, "o" + i, "SRC", "DST" + i,
                        List.of(new Path("path-" + i, "SRC", "DST" + i, 10))))
                .toList();

        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(orders.size());
        AtomicInteger assigned = new AtomicInteger();

        for (TransportOrder order : orders) {
            pool.submit(() -> {
                try {
                    start.await();
                    if (dispatcher.dispatchOrder(order)) {
                        assigned.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdownNow();

        // 最多 3 台车可同时持有订单
        long holding = vehicles.stream().filter(v -> v.getCurrentOrderId() != null).count();
        assertTrue(holding <= 3);
        assertTrue(assigned.get() <= 3);

        Set<String> assignedVehicles = vehicles.stream()
                .filter(v -> v.getCurrentOrderId() != null)
                .map(Vehicle::getVehicleId)
                .collect(Collectors.toSet());
        assertEquals(holding, assignedVehicles.size());

        // 同一车辆不得被多个已分配订单引用
        List<String> processingVehicles = new ArrayList<>();
        for (TransportOrder order : orders) {
            if (order.getProcessingVehicle() != null) {
                processingVehicles.add(order.getProcessingVehicle());
            }
        }
        assertEquals(processingVehicles.size(), Set.copyOf(processingVehicles).size(),
                "同一车辆被多个订单并发占用");
        assertTrue(processingVehicles.size() <= 3);
        assertEquals(assigned.get(), processingVehicles.size());
    }
}
