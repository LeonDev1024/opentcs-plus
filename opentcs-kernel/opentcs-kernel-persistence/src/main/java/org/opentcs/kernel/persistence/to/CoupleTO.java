package org.opentcs.kernel.persistence.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoupleTO implements Serializable {
    private Long x;
    private Long y;
}
