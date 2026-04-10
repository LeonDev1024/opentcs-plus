package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.VehicleTypeDTO;

import java.util.List;
import java.util.Optional;

/**
 * 车辆类型端口接口（Kernel API 层）。
 * <p>
 * 应用层通过此接口操作车辆类型领域模型，具体实现由 infrastructure 层提供。
 * </p>
 */
public interface VehicleTypeApi {

    /**
     * 查询所有车辆类型。
     */
    List<VehicleTypeDTO> findAll();

    /**
     * 根据品牌 ID 查询该品牌下的所有类型。
     */
    List<VehicleTypeDTO> findByBrandId(Long brandId);

    /**
     * 根据数据库 ID 查询。
     */
    Optional<VehicleTypeDTO> findById(Long id);

    /**
     * 根据领域 typeId 查询。
     */
    Optional<VehicleTypeDTO> findByTypeId(String typeId);

    /**
     * 创建车辆类型。
     */
    VehicleTypeDTO create(VehicleTypeDTO vehicleType);

    /**
     * 更新车辆类型。
     */
    VehicleTypeDTO update(VehicleTypeDTO vehicleType);

    /**
     * 删除车辆类型（逻辑删除）。
     */
    void delete(Long id);
}
