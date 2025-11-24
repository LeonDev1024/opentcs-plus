package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.VisualLayout;
import org.opentcs.map.mapper.VisualLayoutMapper;
import org.opentcs.map.service.VisualLayoutService;
import org.springframework.stereotype.Service;

/**
 * 视觉布局 Service 实现类
 */
@Service
public class VisualLayoutServiceImpl extends ServiceImpl<VisualLayoutMapper, VisualLayout> implements VisualLayoutService {

    @Override
    public TableDataInfo<VisualLayout> selectPageVisualLayout(VisualLayout visualLayout, PageQuery pageQuery) {
        IPage<VisualLayout> page = this.getBaseMapper().selectPageVisualLayout(pageQuery.build(), visualLayout);
        return TableDataInfo.build(page);
    }
}