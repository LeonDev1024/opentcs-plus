package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;

import java.util.List;

/**
 * 图层组领域服务接口
 */
public interface LayerGroupRepository extends IService<LayerGroupEntity> {

    /**
     * 分页查询图层组列表
     * @param layerGroup 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LayerGroupEntity> selectPageLayerGroup(LayerGroupEntity layerGroup, PageQuery pageQuery);

    /**
     * 根据导航地图ID查询图层组列表
     * @param navigationMapId 导航地图ID
     * @return 图层组列表
     */
    List<LayerGroupEntity> selectByNavigationMapId(Long navigationMapId);
}
