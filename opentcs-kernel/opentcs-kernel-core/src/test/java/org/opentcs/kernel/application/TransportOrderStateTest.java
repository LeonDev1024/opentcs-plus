package org.opentcs.kernel.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.OrderStep.StepState;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 运输订单状态机测试。
 */
@Tag("dev")
class TransportOrderStateTest {

    @Test
    void shouldActivateOrderWithRouteSteps() {
        TransportOrder order = routedOrder();

        order.activate();

        assertEquals(OrderState.ACTIVE, order.getState());
        assertEquals(StepState.ACTIVE, order.getCurrentStep().getState());
    }

    @Test
    void shouldRejectActivationWithoutSteps() {
        TransportOrder order = new TransportOrder("empty-order");

        assertThrows(IllegalStateException.class, order::activate);
        assertEquals(OrderState.RAW, order.getState());
    }

    @Test
    void shouldFinishAfterAllStepsCompleted() {
        TransportOrder order = routedOrder();
        order.activate();

        order.completeCurrentStep();
        assertEquals(StepState.ACTIVE, order.getCurrentStep().getState());
        order.completeCurrentStep();

        assertEquals(OrderState.FINISHED, order.getState());
    }

    @Test
    void shouldNotCancelFinishedOrder() {
        TransportOrder order = routedOrder();
        order.activate();
        order.complete();

        assertThrows(IllegalStateException.class, order::cancel);
        assertEquals(OrderState.FINISHED, order.getState());
    }

    private TransportOrder routedOrder() {
        return new TransportOrder(
                "order-1",
                "test-order",
                "P1",
                "P3",
                List.of(
                        new Path("path-1", "P1", "P2", 10.0),
                        new Path("path-2", "P2", "P3", 12.0)
                )
        );
    }
}
