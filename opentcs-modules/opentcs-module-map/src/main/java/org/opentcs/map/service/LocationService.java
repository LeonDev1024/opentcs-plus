package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.map.domain.entity.Location;

import java.util.List;

/**
 * 位置 Service 接口
 */
public interface LocationService extends IService<Location> {

    List<Location> selectAllLocationByPlantModelId(Long plantModelId);
}