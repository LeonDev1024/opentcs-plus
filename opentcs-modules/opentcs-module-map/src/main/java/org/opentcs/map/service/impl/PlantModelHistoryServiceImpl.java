package org.opentcs.map.service.impl;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.PlantModelHistory;
import org.opentcs.map.mapper.PlantModelHistoryMapper;
import org.opentcs.map.service.PlantModelHistoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantModelHistoryServiceImpl implements PlantModelHistoryService {

    private final PlantModelHistoryMapper plantModelHistoryMapper;

    @Override
    public void recordHistory(PlantModelHistory history) {
        plantModelHistoryMapper.insert(history);
    }
}

