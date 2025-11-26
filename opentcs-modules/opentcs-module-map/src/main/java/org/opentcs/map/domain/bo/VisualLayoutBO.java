package org.opentcs.map.domain.bo;

import lombok.Data;
import org.opentcs.map.domain.entity.Layer;
import org.opentcs.map.domain.entity.LayerGroup;
import org.opentcs.map.domain.entity.VisualLayout;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class VisualLayoutBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private VisualLayout visualLayout;

    private List<Layer> layers;

    private List<LayerGroup> layerGroups;
}
