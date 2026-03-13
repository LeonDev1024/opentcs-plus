package org.opentcs.map.service;

import org.opentcs.kernel.persistence.entity.PlantModelHistoryEntity;

public interface PlantModelHistoryService {

    /**
     * 记录一次地图模型快照
     */
    void recordHistory(PlantModelHistoryEntity history);
}

