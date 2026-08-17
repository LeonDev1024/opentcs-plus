package org.opentcs.job.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_job")
public class SysJob implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long jobId;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private String cronExpression;
    /** 错误策略 1=立即执行 2=执行一次 3=放弃 */
    private String misfirePolicy;
    /** 并发 0=允许 1=禁止 */
    private String concurrent;
    /** 状态 0=正常 1=暂停 */
    private String status;
    private String remark;
    private String createBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private String updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
