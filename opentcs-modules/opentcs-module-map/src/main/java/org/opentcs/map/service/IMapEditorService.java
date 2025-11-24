package org.opentcs.map.service;

import org.opentcs.map.domain.bo.PlantModelBO;

public interface IMapEditorService {

    PlantModelBO load(String plantModelId);
}
