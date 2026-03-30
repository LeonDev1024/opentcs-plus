package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.opentcs.vehicle.persistence.mapper.VehicleTypeMapper;
import org.opentcs.vehicle.persistence.service.VehicleTypeDomainService;
import org.springframework.stereotype.Service;

/**
 * 车辆类型领域服务实现
 */
@Service
public class VehicleTypeDomainServiceImpl extends ServiceImpl<VehicleTypeMapper, VehicleTypeEntity> implements VehicleTypeDomainService {

    @Override
    public TableDataInfo<VehicleTypeEntity> selectPageVehicleType(VehicleTypeEntity vehicleType, PageQuery pageQuery) {
        Page<VehicleTypeEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<VehicleTypeEntity> result = this.getBaseMapper().selectPageVehicleType(page, vehicleType);
        return TableDataInfo.build(result);
    }
}
