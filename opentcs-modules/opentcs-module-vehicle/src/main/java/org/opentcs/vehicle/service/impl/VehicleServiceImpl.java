package org.opentcs.vehicle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.Vehicle;
import org.opentcs.vehicle.mapper.VehicleMapper;
import org.opentcs.vehicle.service.VehicleService;
import org.springframework.stereotype.Service;

/**
 * 车辆 Service 实现类
 */
@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Override
    public TableDataInfo<Vehicle> selectPageVehicle(Vehicle vehicle, PageQuery pageQuery) {
        return this.getBaseMapper().selectPageVehicle(vehicle, pageQuery);
    }
}