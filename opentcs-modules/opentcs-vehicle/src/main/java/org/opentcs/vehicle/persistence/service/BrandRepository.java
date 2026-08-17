package org.opentcs.vehicle.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.persistence.entity.BrandEntity;

/**
 * 品牌领域服务接口
 */
public interface BrandRepository extends IService<BrandEntity> {

    /**
     * 分页查询品牌列表
     * @param brand 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<BrandEntity> selectPageBrand(BrandEntity brand, PageQuery pageQuery);

    /**
     * 获取所有启用的品牌列表
     * @return 品牌列表
     */
    java.util.List<BrandEntity> selectBrandList();
}
