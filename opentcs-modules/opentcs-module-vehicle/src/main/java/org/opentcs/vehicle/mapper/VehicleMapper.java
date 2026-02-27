package org.opentcs.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.vehicle.domain.entity.Vehicle;

/**
 * 车辆 Mapper 接口
 */
public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 分页查询车辆列表
     * @param page 分页参数
     * @param vehicle 查询条件
     * @return 分页结果
     */
    IPage<Vehicle> selectPageVehicle(IPage<Vehicle> page, Vehicle vehicle);
}