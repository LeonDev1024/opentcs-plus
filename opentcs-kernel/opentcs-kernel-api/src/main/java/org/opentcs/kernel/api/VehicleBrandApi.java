package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.VehicleBrandDTO;

import java.util.List;
import java.util.Optional;

/**
 * 车辆品牌端口接口（Kernel API 层）。
 * <p>
 * 应用层通过此接口操作品牌领域模型，具体实现由 infrastructure 层提供。
 * </p>
 */
public interface VehicleBrandApi {

    /**
     * 查询所有启用的品牌。
     */
    List<VehicleBrandDTO> findAllEnabled();

    /**
     * 根据 ID 查询品牌。
     */
    Optional<VehicleBrandDTO> findById(Long id);

    /**
     * 根据 brandId（领域标识）查询品牌。
     */
    Optional<VehicleBrandDTO> findByBrandId(String brandId);

    /**
     * 创建品牌。
     *
     * @return 持久化后的品牌 DTO（含数据库 id）
     */
    VehicleBrandDTO create(VehicleBrandDTO brand);

    /**
     * 更新品牌。
     */
    VehicleBrandDTO update(VehicleBrandDTO brand);

    /**
     * 删除品牌（逻辑删除）。
     */
    void delete(Long id);

    /**
     * 启用/禁用品牌。
     */
    void changeStatus(Long id, boolean enabled);
}
