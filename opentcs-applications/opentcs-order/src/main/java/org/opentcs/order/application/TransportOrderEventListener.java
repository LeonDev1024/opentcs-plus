package org.opentcs.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.kernel.domain.event.OrderStateChangedEvent;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.service.TransportOrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单领域事件监听器。
 * <p>
 * 将内核运行态订单变化同步到订单持久化模型，逐步收敛应用服务手动双写状态的逻辑。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransportOrderEventListener {

    private final TransportOrderRepository orderRepository;

    @EventListener
    @Transactional
    public void onOrderStateChanged(OrderStateChangedEvent event) {
        TransportOrderEntity entity = orderRepository.getByOrderNo(event.getAggregateId());
        if (entity == null) {
            log.debug("订单状态事件暂未找到持久化记录，跳过: orderId={}, state={}",
                    event.getAggregateId(), event.getNewState());
            return;
        }

        entity.setState(event.getNewState().name());
        entity.setProcessingVehicle(event.getProcessingVehicle());
        if (event.getReason() != null && !event.getReason().isBlank()) {
            Map<String, String> properties = parseProperties(entity.getProperties());
            properties.put("remark", event.getReason());
            entity.setProperties(JsonUtils.toJsonString(properties));
        }
        if (event.getNewState().isFinal()) {
            entity.setFinishedTime(LocalDateTime.ofInstant(
                    event.getTimestamp(), ZoneId.systemDefault()));
        }
        orderRepository.updateById(entity);

        log.debug("订单状态已由领域事件回写: orderId={}, {} -> {}",
                event.getAggregateId(), event.getOldState(), event.getNewState());
    }

    private Map<String, String> parseProperties(String properties) {
        if (properties == null || properties.isBlank()) {
            return new LinkedHashMap<>();
        }
        Map<String, String> parsed = JsonUtils.parseObject(
                properties, new TypeReference<Map<String, String>>() {
                });
        return parsed == null ? new LinkedHashMap<>() : new LinkedHashMap<>(parsed);
    }
}
