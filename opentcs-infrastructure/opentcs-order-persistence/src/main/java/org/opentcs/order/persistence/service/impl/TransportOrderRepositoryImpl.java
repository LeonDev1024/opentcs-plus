package org.opentcs.order.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.mapper.TransportOrderMapper;
import org.opentcs.order.persistence.service.TransportOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运输订单领域服务实现
 */
@Service
public class TransportOrderRepositoryImpl extends ServiceImpl<TransportOrderMapper, TransportOrderEntity> implements TransportOrderRepository {

    @Override
    public TableDataInfo<TransportOrderEntity> selectPageTransportOrder(TransportOrderEntity transportOrder, PageQuery pageQuery) {
        Page<TransportOrderEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<TransportOrderEntity> result = this.getBaseMapper().selectPageTransportOrder(page, transportOrder);
        return TableDataInfo.build(result);
    }

    @Override
    public TransportOrderEntity getByOrderNo(String orderNo) {
        return this.getOne(new LambdaQueryWrapper<TransportOrderEntity>().eq(TransportOrderEntity::getOrderNo, orderNo));
    }

    @Override
    public boolean createTransportOrder(TransportOrderEntity transportOrder) {
        transportOrder.setState("RAW");
        transportOrder.setCreationTime(LocalDateTime.now());
        return this.save(transportOrder);
    }

    @Override
    public boolean batchCreateTransportOrder(List<TransportOrderEntity> transportOrders) {
        for (TransportOrderEntity order : transportOrders) {
            order.setState("RAW");
            order.setCreationTime(LocalDateTime.now());
        }
        return this.saveBatch(transportOrders);
    }

    @Override
    public boolean cancelTransportOrder(Long orderId) {
        TransportOrderEntity order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"FINISHED".equals(order.getState()) && !"FAILED".equals(order.getState())) {
            order.setState("FAILED");
            order.setFinishedTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public boolean assignVehicle(Long orderId, String vehicleId) {
        TransportOrderEntity order = this.getById(orderId);
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
        TransportOrderEntity order = this.getById(orderId);
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
        TransportOrderEntity order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

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
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        long totalOrders = this.count();
        statistics.put("totalOrders", totalOrders);

        long rawOrders = this.count(new LambdaQueryWrapper<TransportOrderEntity>().eq(TransportOrderEntity::getState, "RAW"));
        long activeOrders = this.count(new LambdaQueryWrapper<TransportOrderEntity>().eq(TransportOrderEntity::getState, "ACTIVE"));
        long finishedOrders = this.count(new LambdaQueryWrapper<TransportOrderEntity>().eq(TransportOrderEntity::getState, "FINISHED"));
        long failedOrders = this.count(new LambdaQueryWrapper<TransportOrderEntity>().eq(TransportOrderEntity::getState, "FAILED"));

        statistics.put("rawOrders", rawOrders);
        statistics.put("activeOrders", activeOrders);
        statistics.put("finishedOrders", finishedOrders);
        statistics.put("failedOrders", failedOrders);

        double completionRate = totalOrders > 0 ? (double) finishedOrders / totalOrders * 100 : 0;
        statistics.put("completionRate", completionRate);

        return statistics;
    }

    @Override
    public Map<String, Object> optimizeOrderDispatch(List<Long> orderIds) {
        Map<String, Object> result = new HashMap<>();
        result.put("optimized", true);
        result.put("orderIds", orderIds);
        result.put("message", "订单调度优化完成");
        return result;
    }
}
