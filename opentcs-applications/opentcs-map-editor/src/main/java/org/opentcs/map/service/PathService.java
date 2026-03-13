package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PathEntity;

import java.util.List;

/**
 * 路径 Service 接口
 */
public interface PathService extends IService<PathEntity> {

    /**
     * 分页查询路径列表
     * @param path 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PathEntity> selectPagePath(PathEntity path, PageQuery pageQuery);

    List<PathEntity> selectAllPathByPlantModelId(Long plantModelId);
}