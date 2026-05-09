package org.opentcs.order.application;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.kernel.application.MapRuntimeService;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.service.TransportOrderRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输订单运行态恢复服务。
 * <p>
 * Kernel Runtime 只作为可重建的运行时缓存。应用启动后从数据库未终态订单快照恢复
 * 内核订单，避免服务重启后内核订单上下文丢失。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransportOrderRuntimeRecoveryService {

    private static final String PROPERTY_MAP_ID = "mapId";

    private final TransportOrderRepository orderRepository;
    private final TransportOrderApi transportOrderApi;
    private final MapRuntimeService mapRuntimeService;

    @EventListener(ApplicationReadyEvent.class)
    public void recoverRuntimeOrders() {
        List<TransportOrderEntity> candidates = orderRepository.list().stream()
                .filter(this::shouldRecover)
                .toList();
        if (candidates.isEmpty()) {
            return;
        }

        int recovered = 0;
        int skipped = 0;
        for (TransportOrderEntity entity : candidates) {
            try {
                ensureRuntimeMap(entity);
                OrderState recoveryState = toRecoveryState(entity);
                if (!recoveryState.name().equals(entity.getState())) {
                    entity.setState(recoveryState.name());
                    orderRepository.updateById(entity);
                }
                transportOrderApi.restoreOrder(
                        entity.getOrderNo(),
                        toOrderSpec(entity),
                        OrderStateDTO.valueOf(recoveryState.name()),
                        entity.getProcessingVehicle());
                recovered++;
            } catch (Exception e) {
                skipped++;
                log.warn("订单运行态恢复失败: id={}, orderNo={}, state={}, reason={}",
                        entity.getId(), entity.getOrderNo(), entity.getState(), e.getMessage());
            }
        }

        log.info("订单运行态恢复完成: candidates={}, recovered={}, skipped={}",
                candidates.size(), recovered, skipped);
    }

    private boolean shouldRecover(TransportOrderEntity entity) {
        if (entity == null || entity.getOrderNo() == null || entity.getOrderNo().isBlank()) {
            return false;
        }
        OrderState state = parseState(entity.getState());
        return state != null && !state.isFinal();
    }

    private OrderState toRecoveryState(TransportOrderEntity entity) {
        OrderState state = parseState(entity.getState());
        if (state == OrderState.RAW) {
            return OrderState.RAW;
        }
        return OrderState.RECOVERING;
    }

    private void ensureRuntimeMap(TransportOrderEntity entity) {
        if (mapRuntimeService.getActiveMapId() != null) {
            return;
        }

        String mapId = parseProperties(entity.getProperties()).get(PROPERTY_MAP_ID);
        if (mapId == null || mapId.isBlank()) {
            throw new IllegalStateException("订单缺少 mapId，无法恢复运行地图");
        }
        mapRuntimeService.loadPublishedMap(mapId);
    }

    private OrderSpecDTO toOrderSpec(TransportOrderEntity entity) {
        String[] points = parseDestinations(entity.getDestinations());
        OrderSpecDTO orderSpec = new OrderSpecDTO();
        orderSpec.setName(entity.getName());
        orderSpec.setSourcePointId(points[0]);
        orderSpec.setDestPointId(points[1]);
        orderSpec.setIntendedVehicle(entity.getIntendedVehicle());
        return orderSpec;
    }

    private String[] parseDestinations(String destinations) {
        if (destinations == null || destinations.isBlank()) {
            throw new IllegalArgumentException("订单 destinations 不能为空");
        }
        String[] points = destinations.split(",");
        if (points.length != 2 || points[0].isBlank() || points[1].isBlank()) {
            throw new IllegalArgumentException("订单 destinations 格式应为 sourcePoint,destPoint");
        }
        return new String[] {points[0].trim(), points[1].trim()};
    }

    private OrderState parseState(String state) {
        if (state == null || state.isBlank()) {
            return null;
        }
        try {
            return OrderState.valueOf(state);
        } catch (IllegalArgumentException e) {
            return null;
        }
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
