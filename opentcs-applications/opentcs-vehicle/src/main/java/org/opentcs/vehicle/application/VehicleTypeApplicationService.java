package org.opentcs.vehicle.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.application.bo.VehicleTypeBO;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.opentcs.vehicle.persistence.service.VehicleTypeDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆类型应用服务。
 * <p>
 * Controller 层唯一入口，封装持久化操作，屏蔽 persistence 层实现细节。
 * 接口层只接触 VehicleTypeBO，不接触 VehicleTypeEntity。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class VehicleTypeApplicationService {

    private final VehicleTypeDomainService vehicleTypeDomainService;

    public TableDataInfo<VehicleTypeBO> listVehicleTypes(VehicleTypeBO query, PageQuery pageQuery) {
        TableDataInfo<VehicleTypeEntity> entityPage = vehicleTypeDomainService.selectPageVehicleType(toEntity(query), pageQuery);
        TableDataInfo<VehicleTypeBO> result = new TableDataInfo<>();
        result.setTotal(entityPage.getTotal());
        result.setCode(entityPage.getCode());
        result.setMsg(entityPage.getMsg());
        result.setRows(entityPage.getRows() == null ? List.of()
                : entityPage.getRows().stream().map(this::toBO).collect(Collectors.toList()));
        return result;
    }

    public List<VehicleTypeBO> getAllVehicleTypes() {
        return vehicleTypeDomainService.list().stream()
                .map(this::toBO)
                .collect(Collectors.toList());
    }

    public VehicleTypeBO getById(Long id) {
        return toBO(vehicleTypeDomainService.getById(id));
    }

    public boolean create(VehicleTypeBO vehicleType) {
        return vehicleTypeDomainService.save(toEntity(vehicleType));
    }

    public boolean update(VehicleTypeBO vehicleType) {
        return vehicleTypeDomainService.updateById(toEntity(vehicleType));
    }

    public boolean delete(Long id) {
        return vehicleTypeDomainService.removeById(id);
    }

    public List<VehicleTypeBO> getByBrandId(Long brandId) {
        return vehicleTypeDomainService.list(
                new LambdaQueryWrapper<VehicleTypeEntity>()
                        .eq(VehicleTypeEntity::getBrandId, brandId)
                        .eq(VehicleTypeEntity::getDelFlag, "0")
        ).stream().map(this::toBO).collect(Collectors.toList());
    }

    // ===== 内部转换方法（不对外暴露）=====

    private VehicleTypeBO toBO(VehicleTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        VehicleTypeBO bo = new VehicleTypeBO();
        bo.setId(entity.getId());
        bo.setBrandId(entity.getBrandId());
        bo.setBrandName(entity.getBrandName());
        bo.setName(entity.getName());
        bo.setLength(entity.getLength());
        bo.setWidth(entity.getWidth());
        bo.setHeight(entity.getHeight());
        bo.setMaxVelocity(entity.getMaxVelocity());
        bo.setMaxReverseVelocity(entity.getMaxReverseVelocity());
        bo.setEnergyLevel(entity.getEnergyLevel());
        bo.setAllowedOrders(entity.getAllowedOrders());
        bo.setAllowedPeripheralOperations(entity.getAllowedPeripheralOperations());
        bo.setProperties(entity.getProperties());
        return bo;
    }

    private VehicleTypeEntity toEntity(VehicleTypeBO bo) {
        if (bo == null) {
            return null;
        }
        VehicleTypeEntity entity = new VehicleTypeEntity();
        entity.setId(bo.getId());
        entity.setBrandId(bo.getBrandId());
        entity.setName(bo.getName());
        entity.setLength(bo.getLength());
        entity.setWidth(bo.getWidth());
        entity.setHeight(bo.getHeight());
        entity.setMaxVelocity(bo.getMaxVelocity());
        entity.setMaxReverseVelocity(bo.getMaxReverseVelocity());
        entity.setEnergyLevel(bo.getEnergyLevel());
        entity.setAllowedOrders(bo.getAllowedOrders());
        entity.setAllowedPeripheralOperations(bo.getAllowedPeripheralOperations());
        entity.setProperties(bo.getProperties());
        return entity;
    }
}
