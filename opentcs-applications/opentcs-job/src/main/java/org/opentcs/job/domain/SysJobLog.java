package org.opentcs.job.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_job_log")
public class SysJobLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long jobLogId;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private String jobMessage;
    /** 0=成功 1=失败 */
    private String status;
    private String exceptionInfo;
    private LocalDateTime createTime;
}
