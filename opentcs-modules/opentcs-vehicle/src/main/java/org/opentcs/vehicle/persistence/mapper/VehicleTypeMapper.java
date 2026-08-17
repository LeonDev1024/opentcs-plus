package org.opentcs.vehicle.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;

/**
 * 车辆类型 Mapper 接口
 */
public interface VehicleTypeMapper extends BaseMapper<VehicleTypeEntity> {

    /**
     * 分页查询车辆类型列表
     * @param page 分页参数
     * @param vehicleType 查询条件
     * @return 分页结果
     */
    IPage<VehicleTypeEntity> selectPageVehicleType(IPage<VehicleTypeEntity> page, VehicleTypeEntity vehicleType);
}
