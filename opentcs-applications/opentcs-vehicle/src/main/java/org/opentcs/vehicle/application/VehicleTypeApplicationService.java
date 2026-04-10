package org.opentcs.vehicle.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.opentcs.vehicle.persistence.service.VehicleTypeDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车辆类型应用服务。
 * <p>
 * Controller 层唯一入口，封装持久化操作，屏蔽 persistence 层实现细节。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class VehicleTypeApplicationService {

    private final VehicleTypeDomainService vehicleTypeDomainService;

    public TableDataInfo<VehicleTypeEntity> listVehicleTypes(VehicleTypeEntity query, PageQuery pageQuery) {
        return vehicleTypeDomainService.selectPageVehicleType(query, pageQuery);
    }

    public List<VehicleTypeEntity> getAllVehicleTypes() {
        return vehicleTypeDomainService.list();
    }

    public VehicleTypeEntity getById(Long id) {
        return vehicleTypeDomainService.getById(id);
    }

    public boolean create(VehicleTypeEntity vehicleType) {
        return vehicleTypeDomainService.save(vehicleType);
    }

    public boolean update(VehicleTypeEntity vehicleType) {
        return vehicleTypeDomainService.updateById(vehicleType);
    }

    public boolean delete(Long id) {
        return vehicleTypeDomainService.removeById(id);
    }

    public List<VehicleTypeEntity> getByBrandId(Long brandId) {
        return vehicleTypeDomainService.list(
                new LambdaQueryWrapper<VehicleTypeEntity>()
                        .eq(VehicleTypeEntity::getBrandId, brandId)
                        .eq(VehicleTypeEntity::getDelFlag, "0")
        );
    }
}
