package org.opentcs.job.service;

import org.opentcs.job.domain.SysJob;
import java.util.List;

public interface ISysJobService {
    List<SysJob> listJobs(SysJob query);
    SysJob getById(Long jobId);
    boolean addJob(SysJob job) throws Exception;
    boolean updateJob(SysJob job) throws Exception;
    boolean deleteJobById(Long jobId) throws Exception;
    /** 暂停任务 */
    int pauseJob(SysJob job) throws Exception;
    /** 恢复任务 */
    int resumeJob(SysJob job) throws Exception;
    /** 立即执行一次 */
    boolean run(SysJob job) throws Exception;
    /** 修改任务状态 */
    int changeStatus(SysJob job) throws Exception;
    /** 校验cron是否合法 */
    boolean checkCronExpressionIsValid(String cronExpression);
    /** 获取 cron 接下来 count 次触发时间 */
    List<String> nextTriggerTimes(String cronExpression, int count);
}
