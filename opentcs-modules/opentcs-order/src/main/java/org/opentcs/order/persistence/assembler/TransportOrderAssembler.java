package org.opentcs.order.persistence.assembler;

import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.order.persistence.entity.TransportOrderEntity;

/**
 * 运输订单领域模型与数据模型转换器
 */
public class TransportOrderAssembler {

    /**
     * 将数据模型转换为领域模型
     *
     * @param entity 数据模型
     * @return 领域模型
     */
    public TransportOrder toDomain(TransportOrderEntity entity) {
        if (entity == null) {
            return null;
        }

        TransportOrder order = new TransportOrder(
            String.valueOf(entity.getId()),
            entity.getName()
        );

        // 设置订单状态（通过内部字段）
        if (entity.getState() != null) {
            try {
                OrderState orderState = OrderState.valueOf(entity.getState());
                // 使用反射或通过Builder设置状态
                order = new TransportOrder.Builder(entity.getName())
                    .orderId(String.valueOf(entity.getId()))
                    .build();
                // 由于状态是final，需要在构造后处理
            } catch (IllegalArgumentException e) {
                // 使用默认状态 RAW
            }
        }

        // 设置车辆
        order.setIntendedVehicle(entity.getIntendedVehicle());

        return order;
    }

    /**
     * 从领域模型转换为数据模型
     *
     * @param domain  领域模型
     * @param entity  目标数据模型
     */
    public void toEntity(TransportOrder domain, TransportOrderEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setState(domain.getState() != null ? domain.getState().name() : null);
        entity.setIntendedVehicle(domain.getIntendedVehicle());
        entity.setProcessingVehicle(domain.getProcessingVehicle());
    }

    /**
     * 从领域模型复制属性到数据模型
     *
     * @param domain 领域模型
     * @param entity 目标数据模型
     */
    public void copyToDataModel(TransportOrder domain, TransportOrderEntity entity) {
        toEntity(domain, entity);
    }

    /**
     * 创建领域模型
     */
    public TransportOrder createDomain(String orderId, String name) {
        return new TransportOrder(orderId, name);
    }
}
