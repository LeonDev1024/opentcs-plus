package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PointEntity;

/**
 * 点位 Mapper 接口
 */
public interface PointMapper extends BaseMapper<PointEntity> {

    /**
     * 分页查询点位列表
     * @param point 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery);
}