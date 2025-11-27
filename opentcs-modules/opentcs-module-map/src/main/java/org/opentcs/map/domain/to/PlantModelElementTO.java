package org.opentcs.map.domain.to;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlantModelElementTO {
    private String name = "";
    private List<PropertyTO> properties = new ArrayList<>();
}
