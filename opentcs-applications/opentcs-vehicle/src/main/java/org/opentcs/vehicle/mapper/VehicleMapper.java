package org.opentcs.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.kernel.persistence.entity.VehicleEntity;

/**
 * 车辆 Mapper 接口
 */
public interface VehicleMapper extends BaseMapper<VehicleEntity> {

    /**
     * 分页查询车辆列表
     * @param page 分页参数
     * @param vehicle 查询条件
     * @return 分页结果
     */
    IPage<VehicleEntity> selectPageVehicle(IPage<VehicleEntity> page, VehicleEntity vehicle);
}