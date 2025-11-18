package org.opentcs.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.Vehicle;

/**
 * 车辆 Service 接口
 */
public interface VehicleService extends IService<Vehicle> {

    /**
     * 分页查询车辆列表
     * @param vehicle 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Vehicle> selectPageVehicle(Vehicle vehicle, PageQuery pageQuery);
}