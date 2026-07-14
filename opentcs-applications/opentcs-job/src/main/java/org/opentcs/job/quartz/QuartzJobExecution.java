package org.opentcs.job.quartz;

import org.opentcs.job.domain.SysJob;
import org.quartz.JobExecutionContext;

public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
