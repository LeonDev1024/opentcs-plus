package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.map.domain.entity.VisualLayout;

/**
 * 视觉布局 Mapper 接口
 */
public interface VisualLayoutMapper extends BaseMapper<VisualLayout> {

    /**
     * 分页查询视觉布局列表
     * @param page 分页参数
     * @param visualLayout 查询条件
     * @return 分页结果
     */
    IPage<VisualLayout> selectPageVisualLayout(IPage<VisualLayout> page, VisualLayout visualLayout);
}