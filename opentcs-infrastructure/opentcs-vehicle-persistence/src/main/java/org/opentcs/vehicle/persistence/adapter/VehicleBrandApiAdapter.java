package org.opentcs.vehicle.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.api.VehicleBrandApi;
import org.opentcs.kernel.api.dto.VehicleBrandDTO;
import org.opentcs.vehicle.persistence.entity.BrandEntity;
import org.opentcs.vehicle.persistence.service.BrandRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * VehicleBrandApi 的持久化适配器实现（基础设施层）。
 * 将 kernel-api 端口与 MyBatis Plus 持久化实现桥接。
 */
@Component
@RequiredArgsConstructor
public class VehicleBrandApiAdapter implements VehicleBrandApi {

    private final BrandRepository brandRepository;

    @Override
    public List<VehicleBrandDTO> findAllEnabled() {
        return brandRepository.selectBrandList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleBrandDTO> findById(Long id) {
        BrandEntity entity = brandRepository.getById(id);
        return Optional.ofNullable(entity).map(this::toDTO);
    }

    @Override
    public Optional<VehicleBrandDTO> findByBrandId(String brandId) {
        // brandId 与 code 字段对应（品牌缩写作为领域唯一标识）
        return brandRepository.lambdaQuery()
                .eq(BrandEntity::getCode, brandId)
                .oneOpt()
                .map(this::toDTO);
    }

    @Override
    public VehicleBrandDTO create(VehicleBrandDTO brand) {
        BrandEntity entity = toEntity(brand);
        brandRepository.save(entity);
        return toDTO(entity);
    }

    @Override
    public VehicleBrandDTO update(VehicleBrandDTO brand) {
        BrandEntity entity = toEntity(brand);
        brandRepository.updateById(entity);
        return toDTO(brandRepository.getById(entity.getId()));
    }

    @Override
    public void delete(Long id) {
        brandRepository.removeById(id);
    }

    @Override
    public void changeStatus(Long id, boolean enabled) {
        BrandEntity entity = new BrandEntity();
        entity.setId(id);
        entity.setEnabled(enabled);
        brandRepository.updateById(entity);
    }

    // ===== 转换方法 =====

    private VehicleBrandDTO toDTO(BrandEntity entity) {
        VehicleBrandDTO dto = new VehicleBrandDTO();
        dto.setId(entity.getId());
        dto.setBrandId(entity.getCode());   // code 作为领域 brandId
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setLogo(entity.getLogo());
        dto.setWebsite(entity.getWebsite());
        dto.setDescription(entity.getDescription());
        dto.setContact(entity.getContact());
        dto.setEnabled(entity.getEnabled());
        dto.setSort(entity.getSort());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    private BrandEntity toEntity(VehicleBrandDTO dto) {
        BrandEntity entity = new BrandEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setLogo(dto.getLogo());
        entity.setWebsite(dto.getWebsite());
        entity.setDescription(dto.getDescription());
        entity.setContact(dto.getContact());
        entity.setEnabled(dto.getEnabled());
        entity.setSort(dto.getSort());
        return entity;
    }
}
