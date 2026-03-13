package org.opentcs.map.service;

import org.opentcs.map.domain.entity.PlantModelHistory;

public interface PlantModelHistoryService {

    /**
     * 记录一次地图模型快照
     */
    void recordHistory(PlantModelHistory history);
}

