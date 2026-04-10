package org.opentcs.vehicle.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.BrandEntity;
import org.opentcs.vehicle.persistence.service.BrandDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品牌应用服务。
 * <p>
 * Controller 层唯一入口，封装持久化操作，屏蔽 persistence 层实现细节。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BrandApplicationService {

    private final BrandDomainService brandDomainService;

    public TableDataInfo<BrandEntity> listBrands(BrandEntity query, PageQuery pageQuery) {
        return brandDomainService.selectPageBrand(query, pageQuery);
    }

    public List<BrandEntity> getAllEnabledBrands() {
        return brandDomainService.selectBrandList();
    }

    public BrandEntity getById(Long id) {
        return brandDomainService.getById(id);
    }

    public boolean create(BrandEntity brand) {
        return brandDomainService.save(brand);
    }

    public boolean update(BrandEntity brand) {
        return brandDomainService.updateById(brand);
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
}
