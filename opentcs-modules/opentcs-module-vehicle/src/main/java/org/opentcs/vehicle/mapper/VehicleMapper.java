package org.opentcs.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.Vehicle;

/**
 * 车辆 Mapper 接口
 */
public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 分页查询车辆列表
     * @param vehicle 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Vehicle> selectPageVehicle(Vehicle vehicle, PageQuery pageQuery);
}