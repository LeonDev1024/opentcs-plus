package org.opentcs.vehicle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.VehicleType;
import org.opentcs.vehicle.mapper.VehicleTypeMapper;
import org.opentcs.vehicle.service.VehicleTypeService;
import org.springframework.stereotype.Service;

/**
 * 车辆类型 Service 实现类
 */
@Service
public class VehicleTypeServiceImpl extends ServiceImpl<VehicleTypeMapper, VehicleType> implements VehicleTypeService {

    @Override
    public TableDataInfo<VehicleType> selectPageVehicleType(VehicleType vehicleType, PageQuery pageQuery) {
        return this.getBaseMapper().selectPageVehicleType(vehicleType, pageQuery);
    }
}