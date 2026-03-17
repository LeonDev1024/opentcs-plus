package org.opentcs.map.domain.bo;

import lombok.Data;

import java.util.Date;

/**
 * 地图编辑器业务对象
 */
@Data
public class MapEditorBO {

    private Long id;

    private String name;

    private String data;

    private Date createTime;

    private Date updateTime;
}
