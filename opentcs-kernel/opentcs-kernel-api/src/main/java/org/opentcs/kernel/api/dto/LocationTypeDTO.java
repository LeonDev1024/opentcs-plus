package org.opentcs.kernel.api.dto;

import lombok.Data;
import org.opentcs.common.core.dto.AllowedOperationTO;
import org.opentcs.common.core.dto.AllowedPeripheralOperationTO;
import org.opentcs.common.core.dto.PropertyTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 位置类型数据传输对象
 */
@Data
public class LocationTypeDTO {

    private Long id;

    private String name;

    private List<AllowedOperationTO> allowedOperations;

    private List<AllowedPeripheralOperationTO> allowedPeripheralOperations;

    private List<PropertyTO> properties = new ArrayList<>();

    private Date createTime;

    private Date updateTime;
}
