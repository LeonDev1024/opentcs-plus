package org.opentcs.map.domain.to;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlantModelElementTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name = "";
    private List<PropertyTO> properties = new ArrayList<>();
}
