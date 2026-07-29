package org.opentcs.order.application.bo;

import lombok.Data;

import java.util.Date;

/**
 * 任务模板业务对象
 */
@Data
public class TaskTemplateBO {

    private Long id;

    /** 模板号 */
    private String code;

    /** 模板名称 */
    private String name;

    /** 所属导航地图主键 ID */
    private Long navigationMapId;

    /** 起点点位 ID */
    private String sourcePoint;

    /** 终点点位 ID */
    private String destPoint;

    /** 默认优先级 */
    private Integer priority;

    /** 是否启用 */
    private Boolean enabled;

    /** 备注 */
    private String remark;

    /** 关键词（查询用） */
    private String keyword;

    private Date createTime;

    private Date updateTime;
}
