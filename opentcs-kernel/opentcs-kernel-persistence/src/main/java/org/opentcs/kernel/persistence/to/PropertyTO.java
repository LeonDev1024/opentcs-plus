package org.opentcs.kernel.persistence.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyTO implements Serializable {
    private String name = "";
    private String value = "";
}
