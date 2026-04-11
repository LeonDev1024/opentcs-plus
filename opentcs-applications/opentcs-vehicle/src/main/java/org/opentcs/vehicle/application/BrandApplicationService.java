package org.opentcs.vehicle.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.application.bo.BrandBO;
import org.opentcs.vehicle.persistence.entity.BrandEntity;
import org.opentcs.vehicle.persistence.service.BrandDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌应用服务。
 * <p>
 * Controller 层唯一入口，封装持久化操作，屏蔽 persistence 层实现细节。
 * 接口层只接触 BrandBO，不接触 BrandEntity。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BrandApplicationService {

    private final BrandDomainService brandDomainService;

    public TableDataInfo<BrandBO> listBrands(BrandBO query, PageQuery pageQuery) {
        TableDataInfo<BrandEntity> entityPage = brandDomainService.selectPageBrand(toEntity(query), pageQuery);
        TableDataInfo<BrandBO> result = new TableDataInfo<>();
        result.setTotal(entityPage.getTotal());
        result.setCode(entityPage.getCode());
        result.setMsg(entityPage.getMsg());
        result.setRows(entityPage.getRows() == null ? List.of()
                : entityPage.getRows().stream().map(this::toBO).collect(Collectors.toList()));
        return result;
    }

    public List<BrandBO> getAllEnabledBrands() {
        return brandDomainService.selectBrandList().stream()
                .map(this::toBO)
                .collect(Collectors.toList());
    }

    public BrandBO getById(Long id) {
        return toBO(brandDomainService.getById(id));
    }

    public boolean create(BrandBO brand) {
        return brandDomainService.save(toEntity(brand));
    }

    public boolean update(BrandBO brand) {
        return brandDomainService.updateById(toEntity(brand));
    }

    public boolean delete(Long id) {
        return brandDomainService.removeById(id);
    }

    public boolean changeStatus(Long id, Boolean enabled) {
        BrandEntity entity = new BrandEntity();
        entity.setId(id);
        entity.setEnabled(enabled);
        return brandDomainService.updateById(entity);
    }

    // ===== 内部转换方法（不对外暴露）=====

    private BrandBO toBO(BrandEntity entity) {
        if (entity == null) {
            return null;
        }
        BrandBO bo = new BrandBO();
        bo.setId(entity.getId());
        bo.setName(entity.getName());
        bo.setCode(entity.getCode());
        bo.setLogo(entity.getLogo());
        bo.setWebsite(entity.getWebsite());
        bo.setDescription(entity.getDescription());
        bo.setContact(entity.getContact());
        bo.setEnabled(entity.getEnabled());
        bo.setSort(entity.getSort());
        return bo;
    }

    private BrandEntity toEntity(BrandBO bo) {
        if (bo == null) {
            return null;
        }
        BrandEntity entity = new BrandEntity();
        entity.setId(bo.getId());
        entity.setName(bo.getName());
        entity.setCode(bo.getCode());
        entity.setLogo(bo.getLogo());
        entity.setWebsite(bo.getWebsite());
        entity.setDescription(bo.getDescription());
        entity.setContact(bo.getContact());
        entity.setEnabled(bo.getEnabled());
        entity.setSort(bo.getSort());
        return entity;
    }
}
