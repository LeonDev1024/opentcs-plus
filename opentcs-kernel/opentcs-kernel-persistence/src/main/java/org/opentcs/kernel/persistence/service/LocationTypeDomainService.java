package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;

import java.util.List;

/**
 * 位置类型领域服务接口
 */
public interface LocationTypeDomainService extends IService<LocationTypeEntity> {

    /**
     * 分页查询位置类型列表
     * @param locationType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationTypeEntity> selectPage(LocationTypeEntity locationType, PageQuery pageQuery);

    /**
     * 查询所有位置类型
     * @return 位置类型列表
     */
    List<LocationTypeEntity> selectAll();

    /**
     * 根据ID查询位置类型
     * @param id 位置类型ID
     * @return 位置类型
     */
    LocationTypeEntity selectById(Long id);
}
