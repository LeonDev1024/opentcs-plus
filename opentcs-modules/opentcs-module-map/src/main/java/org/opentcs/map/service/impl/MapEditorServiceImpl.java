package org.opentcs.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.service.IMapEditorService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapEditorServiceImpl implements IMapEditorService {
    @Override
    public PlantModelBO load(LoadModelVO loadModelVO) {
        return null;
    }
}
