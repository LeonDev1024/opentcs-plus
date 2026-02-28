package org.opentcs.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.Vehicle;
import java.util.Map;
import java.util.List;

/**
 * 车辆 Service 接口
 */
public interface VehicleService extends IService<Vehicle> {

    /**
     * 分页查询车辆列表
     * @param vehicle 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Vehicle> selectPageVehicle(Vehicle vehicle, PageQuery pageQuery);

    /**
     * 获取车辆实时状态
     * @param vehicleId 车辆ID
     * @return 车辆状态
     */
    Vehicle getVehicleStatus(Long vehicleId);

    /**
     * 获取所有车辆状态
     * @return 车辆状态列表
     */
    List<Vehicle> getAllVehicleStatus();

    /**
     * 远程控制车辆
     * @param vehicleId 车辆ID
     * @param command 控制命令
     * @param params 命令参数
     * @return 控制结果
     */
    String controlVehicle(Long vehicleId, String command, Map<String, Object> params);

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
    boolean registerVehicle(Vehicle vehicle);

    /**
     * 车辆注销
     * @param vehicleId 车辆ID
     * @return 注销结果
     */
    boolean unregisterVehicle(Long vehicleId);
}