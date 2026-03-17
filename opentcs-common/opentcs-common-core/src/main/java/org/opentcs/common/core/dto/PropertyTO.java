package org.opentcs.common.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
}
