package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.map.domain.entity.LocationType;

import java.util.List;

/**
 * 位置类型 Service 接口
 */
public interface LocationTypeService extends IService<LocationType> {
    List<LocationType> selectAllLocationTypeByPlantModelId(Long plantModelId);
}