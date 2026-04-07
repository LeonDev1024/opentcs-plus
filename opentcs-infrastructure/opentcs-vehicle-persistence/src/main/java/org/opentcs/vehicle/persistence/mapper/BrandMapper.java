package org.opentcs.vehicle.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.vehicle.persistence.entity.BrandEntity;

/**
 * 品牌 Mapper 接口
 */
public interface BrandMapper extends BaseMapper<BrandEntity> {

    /**
     * 分页查询品牌列表
     * @param page 分页参数
     * @param brand 查询条件
     * @return 分页结果
     */
    IPage<BrandEntity> selectPageBrand(IPage<BrandEntity> page, BrandEntity brand);
}
