package org.opentcs.order.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.domain.entity.TransportOrder;
import org.opentcs.order.mapper.TransportOrderMapper;
import org.opentcs.order.service.TransportOrderService;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 运输订单 Service 实现类
 */
@Service
public class TransportOrderServiceImpl extends ServiceImpl<TransportOrderMapper, TransportOrder> implements TransportOrderService {

    @Override
    public TableDataInfo<TransportOrder> selectPageTransportOrder(TransportOrder transportOrder, PageQuery pageQuery) {
        Page<TransportOrder> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<TransportOrder> result = this.getBaseMapper().selectPageTransportOrder(page, transportOrder);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean createTransportOrder(TransportOrder transportOrder) {
        // 设置默认状态
        transportOrder.setState("RAW");
        transportOrder.setCreationTime(LocalDateTime.now());
        return this.save(transportOrder);
    }

    @Override
    public boolean batchCreateTransportOrder(List<TransportOrder> transportOrders) {
        // 为每个订单设置默认状态和创建时间
        for (TransportOrder order : transportOrders) {
            order.setState("RAW");
            order.setCreationTime(LocalDateTime.now());
        }
        return this.saveBatch(transportOrders);
    }

    @Override
    public boolean cancelTransportOrder(Long orderId) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有未完成的订单可以取消
        if (!"FINISHED".equals(order.getState()) && !"FAILED".equals(order.getState())) {
            order.setState("FAILED");
            order.setFinishedTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public boolean assignVehicle(Long orderId, String vehicleId) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setIntendedVehicle(vehicleId);
        order.setProcessingVehicle(vehicleId);
        order.setState("ACTIVE");
        return this.updateById(order);
    }

    @Override
    public Map<String, Object> getOrderStatus(Long orderId) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Map<String, Object> status = new HashMap<>();
        status.put("orderId", order.getId());
        status.put("orderName", order.getName());
        status.put("state", order.getState());
        status.put("intendedVehicle", order.getIntendedVehicle());
        status.put("processingVehicle", order.getProcessingVehicle());
        status.put("destinations", order.getDestinations());
        status.put("creationTime", order.getCreationTime());
        status.put("finishedTime", order.getFinishedTime());
        status.put("deadline", order.getDeadline());

        return status;
    }

    @Override
    public TableDataInfo<Map<String, Object>> getOrderHistory(Long orderId, PageQuery pageQuery) {
        TransportOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 这里简化处理，实际应该从历史表中查询
        // 模拟历史数据
        List<Map<String, Object>> history = new ArrayList<>();
        Map<String, Object> record1 = new HashMap<>();
        record1.put("time", order.getCreationTime());
        record1.put("action", "创建订单");
        record1.put("details", "创建运输订单 " + order.getName());
        history.add(record1);

        if (order.getProcessingVehicle() != null) {
            Map<String, Object> record2 = new HashMap<>();
            record2.put("time", LocalDateTime.now().minusHours(1));
            record2.put("action", "分配车辆");
            record2.put("details", "分配车辆 " + order.getProcessingVehicle());
            history.add(record2);
        }

        if ("ACTIVE".equals(order.getState())) {
            Map<String, Object> record3 = new HashMap<>();
            record3.put("time", LocalDateTime.now().minusMinutes(30));
            record3.put("action", "开始执行");
            record3.put("details", "车辆开始执行订单");
            history.add(record3);
        }

        if ("FINISHED".equals(order.getState())) {
            Map<String, Object> record4 = new HashMap<>();
            record4.put("time", order.getFinishedTime());
            record4.put("action", "完成订单");
            record4.put("details", "订单执行完成");
            history.add(record4);
        }

        if ("FAILED".equals(order.getState())) {
            Map<String, Object> record5 = new HashMap<>();
            record5.put("time", order.getFinishedTime());
            record5.put("action", "订单失败");
            record5.put("details", "订单执行失败");
            history.add(record5);
        }

        // 构建分页结果
        int pageSize = pageQuery.getPageSize();
        int pageNum = pageQuery.getPageNum();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, history.size());
        List<Map<String, Object>> pageData = history.subList(start, end);

        TableDataInfo<Map<String, Object>> result = new TableDataInfo<>();
        result.setRows(pageData);
        result.setTotal(history.size());

        return result;
    }

    @Override
    public Map<String, Object> optimizeOrderDispatch(List<Long> orderIds) {
        // 这里简化处理，实际应该实现更复杂的调度优化算法
        Map<String, Object> result = new HashMap<>();
        result.put("optimized", true);
        result.put("orderIds", orderIds);
        result.put("message", "订单调度优化完成");
        return result;
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总订单数
        long totalOrders = this.count();
        statistics.put("totalOrders", totalOrders);

        // 各状态订单数
        long rawOrders = this.count(new LambdaQueryWrapper<>(TransportOrder.class).eq(TransportOrder::getState, "RAW"));
        long activeOrders = this.count(new LambdaQueryWrapper<>(TransportOrder.class).eq(TransportOrder::getState, "ACTIVE"));
        long finishedOrders = this.count(new LambdaQueryWrapper<>(TransportOrder.class).eq(TransportOrder::getState, "FINISHED"));
        long failedOrders = this.count(new LambdaQueryWrapper<>(TransportOrder.class).eq(TransportOrder::getState, "FAILED"));

        statistics.put("rawOrders", rawOrders);
        statistics.put("activeOrders", activeOrders);
        statistics.put("finishedOrders", finishedOrders);
        statistics.put("failedOrders", failedOrders);

        // 计算完成率
        double completionRate = totalOrders > 0 ? (double) finishedOrders / totalOrders * 100 : 0;
        statistics.put("completionRate", completionRate);

        return statistics;
    }
}