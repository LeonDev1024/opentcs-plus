package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.VisualLayout;

/**
 * 视觉布局 Service 接口
 */
public interface VisualLayoutService extends IService<VisualLayout> {

    /**
     * 分页查询视觉布局列表
     * @param visualLayout 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<VisualLayout> selectPageVisualLayout(VisualLayout visualLayout, PageQuery pageQuery);
}