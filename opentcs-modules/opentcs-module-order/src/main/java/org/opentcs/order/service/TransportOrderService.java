package org.opentcs.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.domain.entity.TransportOrder;
import java.util.Map;
import java.util.List;

/**
 * 运输订单 Service 接口
 */
public interface TransportOrderService extends IService<TransportOrder> {

    /**
     * 分页查询运输订单列表
     * @param transportOrder 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<TransportOrder> selectPageTransportOrder(TransportOrder transportOrder, PageQuery pageQuery);

    /**
     * 创建运输订单
     * @param transportOrder 订单信息
     * @return 创建结果
     */
    boolean createTransportOrder(TransportOrder transportOrder);

    /**
     * 批量创建运输订单
     * @param transportOrders 订单列表
     * @return 创建结果
     */
    boolean batchCreateTransportOrder(List<TransportOrder> transportOrders);

    /**
     * 取消运输订单
     * @param orderId 订单ID
     * @return 取消结果
     */
    boolean cancelTransportOrder(Long orderId);

    /**
     * 分配车辆
     * @param orderId 订单ID
     * @param vehicleId 车辆ID
     * @return 分配结果
     */
    boolean assignVehicle(Long orderId, String vehicleId);

    /**
     * 获取订单执行状态
     * @param orderId 订单ID
     * @return 执行状态
     */
    Map<String, Object> getOrderStatus(Long orderId);

    /**
     * 获取订单执行历史
     * @param orderId 订单ID
     * @param pageQuery 分页参数
     * @return 执行历史
     */
    TableDataInfo<Map<String, Object>> getOrderHistory(Long orderId, PageQuery pageQuery);

    /**
     * 优化订单调度
     * @param orderIds 订单ID列表
     * @return 优化结果
     */
    Map<String, Object> optimizeOrderDispatch(List<Long> orderIds);

    /**
     * 获取订单统计数据
     * @return 统计数据
     */
    Map<String, Object> getOrderStatistics();
}