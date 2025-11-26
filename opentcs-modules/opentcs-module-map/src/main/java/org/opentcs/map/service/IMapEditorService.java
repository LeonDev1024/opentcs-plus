package org.opentcs.map.service;

import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.vo.LoadModelVO;

public interface IMapEditorService {

    PlantModelBO load(LoadModelVO loadModelVO);

    boolean save(PlantModelBO plantModelBO);
}
