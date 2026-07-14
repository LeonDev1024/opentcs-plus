package org.opentcs.job.quartz;

import org.opentcs.job.domain.SysJob;
import org.opentcs.job.domain.SysJobLog;
import org.opentcs.job.service.ISysJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Slf4j
public abstract class AbstractQuartzJob implements Job {

    @Autowired
    private ISysJobLogService jobLogService;

    @Override
    public void execute(JobExecutionContext context) {
        SysJob sysJob = (SysJob) context.getMergedJobDataMap().get(ScheduleUtils.TASK_PROPERTIES);
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobName(sysJob.getJobName());
        jobLog.setJobGroup(sysJob.getJobGroup());
        jobLog.setInvokeTarget(sysJob.getInvokeTarget());
        jobLog.setCreateTime(LocalDateTime.now());

        long startTime = System.currentTimeMillis();
        try {
            doExecute(context, sysJob);
            long elapsed = System.currentTimeMillis() - startTime;
            jobLog.setJobMessage(sysJob.getJobName() + " 执行成功，耗时：" + elapsed + " 毫秒");
            jobLog.setStatus("0");
        } catch (JobBeanUnavailableException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            String message = sysJob.getJobName() + " 已跳过（" + e.getMessage() + "），耗时：" + elapsed + " 毫秒";
            log.warn("任务跳过 [{}]：{}", sysJob.getJobName(), e.getMessage());
            jobLog.setJobMessage(message);
            jobLog.setStatus("0");
        } catch (Exception e) {
            log.error("任务执行失败 [{}]", sysJob.getJobName(), e);
            long elapsed = System.currentTimeMillis() - startTime;
            jobLog.setJobMessage(sysJob.getJobName() + " 执行失败，耗时：" + elapsed + " 毫秒");
            jobLog.setStatus("1");
            String exMsg = e.getMessage();
            jobLog.setExceptionInfo(exMsg != null && exMsg.length() > 2000
                    ? exMsg.substring(0, 2000) : exMsg);
        } finally {
            jobLogService.addLog(jobLog);
        }
    }

    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
