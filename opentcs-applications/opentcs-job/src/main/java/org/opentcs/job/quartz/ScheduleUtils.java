package org.opentcs.job.quartz;

import org.opentcs.job.domain.SysJob;
import org.quartz.*;

public class ScheduleUtils {

    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";
    private static final String JOB_KEY_PREFIX = "TASK_";

    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(JOB_KEY_PREFIX + jobId, jobGroup);
    }

    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(JOB_KEY_PREFIX + jobId, jobGroup);
    }

    /** 创建任务 */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException {
        Class<? extends Job> jobClass = "0".equals(job.getConcurrent())
                ? QuartzJobExecution.class
                : QuartzDisallowConcurrentExecution.class;

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(getJobKey(job.getJobId(), job.getJobGroup()))
                .build();
        jobDetail.getJobDataMap().put(TASK_PROPERTIES, job);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        scheduleBuilder = handleCronScheduleMisfirePolicy(job, scheduleBuilder);

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(job.getJobId(), job.getJobGroup()))
                .withSchedule(scheduleBuilder)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停状态则暂停任务
        if ("1".equals(job.getStatus())) {
            scheduler.pauseJob(getJobKey(job.getJobId(), job.getJobGroup()));
        }
    }

    /** 更新任务 */
    public static void updateScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(job.getJobId(), job.getJobGroup());
        if (scheduler.checkExists(triggerKey)) {
            scheduler.unscheduleJob(triggerKey);
        }
        createScheduleJob(scheduler, job);
    }

    /** 删除任务 */
    public static void deleteScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException {
        scheduler.deleteJob(getJobKey(job.getJobId(), job.getJobGroup()));
    }

    private static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder builder) {
        return switch (job.getMisfirePolicy()) {
            case "1" -> builder.withMisfireHandlingInstructionIgnoreMisfires();
            case "2" -> builder.withMisfireHandlingInstructionFireAndProceed();
            default  -> builder.withMisfireHandlingInstructionDoNothing();
        };
    }
}
