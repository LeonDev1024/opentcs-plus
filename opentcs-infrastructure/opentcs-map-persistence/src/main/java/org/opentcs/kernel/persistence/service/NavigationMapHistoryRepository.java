package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.kernel.persistence.entity.NavigationMapHistoryEntity;

/**
 * 导航地图历史版本领域服务接口。
 */
public interface NavigationMapHistoryRepository extends IService<NavigationMapHistoryEntity> {

    /**
     * 记录地图快照历史。
     *
     * @param navigationMapId 地图ID
     * @param mapVersion 地图版本
     * @param snapshotUrl 快照地址
     * @param changeSummary 变更摘要
     */
    void recordSnapshot(Long navigationMapId, String mapVersion, String snapshotUrl, String changeSummary);
}
