package org.opentcs.vehicle.persistence.service;

import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;

/**
 * 车辆类型领域服务接口
 */
public interface VehicleTypeDomainService extends com.baomidou.mybatisplus.extension.service.IService<VehicleTypeEntity> {

    /**
     * 分页查询车辆类型列表
     * @param vehicleType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<VehicleTypeEntity> selectPageVehicleType(VehicleTypeEntity vehicleType, PageQuery pageQuery);
}
