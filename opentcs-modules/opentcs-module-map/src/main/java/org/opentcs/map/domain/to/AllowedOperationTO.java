package org.opentcs.map.domain.to;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AllowedOperationTO extends PlantModelElementTO implements Serializable {
}
