package org.opentcs.vehicle.persistence.service;

import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;

import java.util.List;
import java.util.Map;

/**
 * 车辆领域服务接口
 * 定义车辆数据的持久化操作，供应用层使用
 */
public interface VehicleRepository extends com.baomidou.mybatisplus.extension.service.IService<VehicleEntity> {

    /**
     * 分页查询车辆列表
     * @param vehicle 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<VehicleEntity> selectPageVehicle(VehicleEntity vehicle, PageQuery pageQuery);

    /**
     * 获取车辆实时状态
     * @param vehicleId 车辆ID
     * @return 车辆状态
     */
    VehicleEntity getVehicleStatus(Long vehicleId);

    /**
     * 获取所有车辆状态
     * @return 车辆状态列表
     */
    List<VehicleEntity> getAllVehicleStatus();

    /**
     * 获取车辆统计数据
     * @return 统计数据
     */
    Map<String, Object> getVehicleStatistics();

    /**
     * 获取车辆运行历史
     * @param vehicleId 车辆ID
     * @param pageQuery 分页参数
     * @return 运行历史
     */
    TableDataInfo<Map<String, Object>> getVehicleHistory(Long vehicleId, PageQuery pageQuery);

    /**
     * 车辆注册
     * @param vehicle 车辆信息
     * @return 注册结果
     */
    boolean registerVehicle(VehicleEntity vehicle);

    /**
     * 车辆注销
     * @param vehicleId 车辆ID
     * @return 注销结果
     */
    boolean unregisterVehicle(Long vehicleId);

    /**
     * 根据名称查询车辆
     * @param name 车辆名称
     * @return 车辆实体
     */
    VehicleEntity getByName(String name);
}
