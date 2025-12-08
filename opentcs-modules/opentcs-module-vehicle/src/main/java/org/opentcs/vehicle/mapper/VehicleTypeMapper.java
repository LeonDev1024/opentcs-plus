package org.opentcs.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.vehicle.domain.entity.VehicleType;

/**
 * 车辆类型 Mapper 接口
 */
public interface VehicleTypeMapper extends BaseMapper<VehicleType> {

    /**
     * 分页查询车辆类型列表
     * @param page 分页参数
     * @param vehicleType 查询条件
     * @return 分页结果
     */
    IPage<VehicleType> selectPageVehicleType(IPage<VehicleType> page, VehicleType vehicleType);
}