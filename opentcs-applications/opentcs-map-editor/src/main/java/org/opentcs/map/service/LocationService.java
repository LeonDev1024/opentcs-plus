package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.kernel.persistence.entity.LocationEntity;

import java.util.List;

/**
 * 位置 Service 接口
 */
public interface LocationService extends IService<LocationEntity> {

    List<LocationEntity> selectAllLocationByPlantModelId(Long plantModelId);
}