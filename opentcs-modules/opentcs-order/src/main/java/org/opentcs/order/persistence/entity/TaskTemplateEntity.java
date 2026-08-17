package org.opentcs.order.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务模板（极简：模板号 + 起点/终点 + 默认优先级）
 */
@Data
@TableName("tcs_task_template")
public class TaskTemplateEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    /** 默认优先级，越大越优先 */
    private Integer priority;

    /** 是否启用 */
    private Boolean enabled;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    /** 关键词（模板号或名称，仅查询条件） */
    @TableField(exist = false)
    private String keyword;
}
