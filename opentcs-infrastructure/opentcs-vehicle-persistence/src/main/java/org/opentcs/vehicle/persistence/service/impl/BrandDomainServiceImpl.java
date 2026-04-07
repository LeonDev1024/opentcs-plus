package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.BrandEntity;
import org.opentcs.vehicle.persistence.mapper.BrandMapper;
import org.opentcs.vehicle.persistence.service.BrandDomainService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 品牌领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrandDomainServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements BrandDomainService {

    @Override
    public TableDataInfo<BrandEntity> selectPageBrand(BrandEntity brand, PageQuery pageQuery) {
        LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<BrandEntity>()
                .like(StringUtils.hasText(brand.getName()), BrandEntity::getName, brand.getName())
                .like(StringUtils.hasText(brand.getCode()), BrandEntity::getCode, brand.getCode())
                .eq(brand.getEnabled() != null, BrandEntity::getEnabled, brand.getEnabled())
                .eq(BrandEntity::getDelFlag, "0")
                .orderByAsc(BrandEntity::getSort)
                .orderByDesc(BrandEntity::getCreateTime);

        Page<BrandEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<BrandEntity> result = this.page(page, wrapper);

        return TableDataInfo.build(result);
    }

    @Override
    public List<BrandEntity> selectBrandList() {
        LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<BrandEntity>()
                .eq(BrandEntity::getEnabled, true)
                .eq(BrandEntity::getDelFlag, "0")
                .orderByAsc(BrandEntity::getSort);

        return this.list(wrapper);
    }
}
