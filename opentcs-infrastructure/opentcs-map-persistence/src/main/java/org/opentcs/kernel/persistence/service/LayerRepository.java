package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerEntity;

import java.util.List;

/**
 * 图层领域服务接口
 */
public interface LayerRepository extends IService<LayerEntity> {

    /**
     * 分页查询图层列表
     * @param layer 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LayerEntity> selectPageLayer(LayerEntity layer, PageQuery pageQuery);

    /**
     * 根据导航地图ID查询图层列表
     * @param navigationMapId 导航地图ID
     * @return 图层列表
     */
    List<LayerEntity> selectByNavigationMapId(Long navigationMapId);
}
