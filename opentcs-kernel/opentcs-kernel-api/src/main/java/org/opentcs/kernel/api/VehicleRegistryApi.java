package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.kernel.api.dto.VehicleStateDTO;
import org.opentcs.kernel.api.dto.PositionDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 车辆注册 API
 * 负责车辆的注册、状态管理、位置更新
 */
public interface VehicleRegistryApi {

    /**
     * 注册车辆
     * @param vehicle 车辆信息
     * @return 注册后的车辆（含ID）
     */
    VehicleDTO registerVehicle(VehicleDTO vehicle);

    /**
     * 注销车辆
     * @param vehicleId 车辆ID
     */
    void unregisterVehicle(String vehicleId);

    /**
     * 更新车辆状态
     * @param vehicleId 车辆ID
     * @param state 车辆状态
     */
    void updateVehicleState(String vehicleId, VehicleStateDTO state);

    /**
     * 更新车辆位置
     * @param vehicleId 车辆ID
     * @param position 位置信息
     */
    void updateVehiclePosition(String vehicleId, PositionDTO position);

    /**
     * 获取车辆
     * @param vehicleId 车辆ID
     * @return 车辆信息
     */
    Optional<VehicleDTO> getVehicle(String vehicleId);

    /**
     * 获取所有车辆
     * @return 车辆列表
     */
    List<VehicleDTO> getAllVehicles();

    /**
     * 获取可用车辆（空闲且未绑定订单）
     * @return 可用车辆列表
     */
    List<VehicleDTO> getAvailableVehicles();

    /**
     * 获取所有在线车辆ID
     * @return 在线车辆ID集合
     */
    Set<String> getOnlineVehicleIds();

    /**
     * 检查车辆是否存在
     * @param vehicleId 车辆ID
     * @return 是否存在
     */
    boolean vehicleExists(String vehicleId);
}
