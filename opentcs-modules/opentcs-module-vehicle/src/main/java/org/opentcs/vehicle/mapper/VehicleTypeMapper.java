package org.opentcs.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.VehicleType;

/**
 * 车辆类型 Mapper 接口
 */
public interface VehicleTypeMapper extends BaseMapper<VehicleType> {

    /**
     * 分页查询车辆类型列表
     * @param vehicleType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<VehicleType> selectPageVehicleType(VehicleType vehicleType, PageQuery pageQuery);
}