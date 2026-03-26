package org.opentcs.kernel.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.kernel.persistence.entity.NavigationMapHistoryEntity;

import java.util.List;

/**
 * 导航地图历史版本 Mapper 接口
 */
public interface NavigationMapHistoryMapper extends BaseMapper<NavigationMapHistoryEntity> {

    /**
     * 根据地图ID查询所有历史版本
     * @param navigationMapId 地图ID
     * @return 历史版本列表
     */
    List<NavigationMapHistoryEntity> selectByNavigationMapId(Long navigationMapId);
}
