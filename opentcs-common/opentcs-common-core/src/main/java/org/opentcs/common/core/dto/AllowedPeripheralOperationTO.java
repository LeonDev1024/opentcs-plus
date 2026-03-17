package org.opentcs.common.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AllowedPeripheralOperationTO extends PlantModelElementTO implements Serializable {
}
