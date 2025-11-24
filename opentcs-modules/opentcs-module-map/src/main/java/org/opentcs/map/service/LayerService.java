package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.Layer;

/**
 * 图层 Service 接口
 */
public interface LayerService extends IService<Layer> {

    /**
     * 分页查询图层列表
     * @param layer 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Layer> selectPageLayer(Layer layer, PageQuery pageQuery);
}