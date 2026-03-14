package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;

/**
 * 位置类型 Service 接口
 */
public interface LocationTypeService extends IService<LocationTypeEntity> {
    TableDataInfo<LocationTypeEntity> selectPage(LocationTypeEntity locationType, PageQuery pageQuery);
}