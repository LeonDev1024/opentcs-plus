package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PointEntity;

import java.util.List;

/**
 * 点位 Service 接口
 */
public interface PointService extends IService<PointEntity> {

    /**
     * 分页查询点位列表
     * @param point 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery);

    List<PointEntity> selectAllPointByPlantModelId(Long plantModelId);
}