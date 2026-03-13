package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.bo.VisualLayoutBO;
import org.opentcs.kernel.persistence.entity.VisualLayoutEntity;

/**
 * 视觉布局 Service 接口
 */
public interface VisualLayoutService extends IService<VisualLayoutEntity> {

    VisualLayoutBO getVisualLayoutByPlantModelId(Long plantModelId);

    /**
     * 分页查询视觉布局列表
     * @param visualLayout 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<VisualLayoutEntity> selectPageVisualLayout(VisualLayoutEntity visualLayout, PageQuery pageQuery);
}