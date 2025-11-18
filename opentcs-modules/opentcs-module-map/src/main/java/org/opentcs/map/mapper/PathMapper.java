package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.Path;

/**
 * 路径 Mapper 接口
 */
public interface PathMapper extends BaseMapper<Path> {

    /**
     * 分页查询路径列表
     * @param path 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Path> selectPagePath(Path path, PageQuery pageQuery);
}