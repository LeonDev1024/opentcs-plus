package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.LocationType;

import java.util.List;

/**
 * 位置类型 Service 接口
 */
public interface LocationTypeService extends IService<LocationType> {
    TableDataInfo<LocationType> selectPage(LocationType locationType, PageQuery pageQuery);
}