package org.opentcs.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.opentcs.job.domain.SysJob;
import org.opentcs.job.mapper.SysJobMapper;
import org.opentcs.job.quartz.ScheduleUtils;
import org.opentcs.job.service.ISysJobService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl implements ISysJobService {

    private final SysJobMapper jobMapper;
    private final Scheduler scheduler;

    /** 启动时将 DB 里所有任务同步到 Quartz（异常只记录日志，不阻断启动） */
    @PostConstruct
    public void init() {
        try {
            scheduler.clear();
            List<SysJob> jobs = jobMapper.selectList(null);
            for (SysJob job : jobs) {
                ScheduleUtils.createScheduleJob(scheduler, job);
            }
            log.info("Quartz 初始化完成，共加载 {} 个任务", jobs.size());
        } catch (Exception e) {
            log.warn("Quartz 初始化失败（表可能尚未创建），跳过任务加载: {}", e.getMessage());
        }
    }

    @Override
    public List<SysJob> listJobs(SysJob query) {
        LambdaQueryWrapper<SysJob> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getJobName())) {
            wrapper.like(SysJob::getJobName, query.getJobName());
        }
        if (StringUtils.hasText(query.getJobGroup())) {
            wrapper.eq(SysJob::getJobGroup, query.getJobGroup());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysJob::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysJob::getJobId);
        return jobMapper.selectList(wrapper);
    }

    @Override
    public SysJob getById(Long jobId) {
        return jobMapper.selectById(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJob(SysJob job) throws Exception {
        job.setStatus("1"); // 新增默认暂停
        int rows = jobMapper.insert(job);
        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(SysJob job) throws Exception {
        int rows = jobMapper.updateById(job);
        if (rows > 0) {
            ScheduleUtils.updateScheduleJob(scheduler, job);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobById(Long jobId) throws Exception {
        SysJob job = jobMapper.selectById(jobId);
        int rows = jobMapper.deleteById(jobId);
        if (rows > 0 && job != null) {
            ScheduleUtils.deleteScheduleJob(scheduler, job);
        }
        return rows > 0;
    }

    @Override
    public int pauseJob(SysJob job) throws Exception {
        SysJob sysJob = jobMapper.selectById(job.getJobId());
        sysJob.setStatus("1");
        int rows = jobMapper.updateById(sysJob);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(sysJob.getJobId(), sysJob.getJobGroup()));
        }
        return rows;
    }

    @Override
    public int resumeJob(SysJob job) throws Exception {
        SysJob sysJob = jobMapper.selectById(job.getJobId());
        sysJob.setStatus("0");
        int rows = jobMapper.updateById(sysJob);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(sysJob.getJobId(), sysJob.getJobGroup()));
        }
        return rows;
    }

    @Override
    public boolean run(SysJob job) throws Exception {
        SysJob sysJob = jobMapper.selectById(job.getJobId());
        JobKey jobKey = ScheduleUtils.getJobKey(sysJob.getJobId(), sysJob.getJobGroup());
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleUtils.TASK_PROPERTIES, sysJob);
        scheduler.triggerJob(jobKey, dataMap);
        return true;
    }

    @Override
    public int changeStatus(SysJob job) throws Exception {
        if ("0".equals(job.getStatus())) {
            return resumeJob(job);
        } else {
            return pauseJob(job);
        }
    }

    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    @Override
    public List<String> nextTriggerTimes(String cronExpression, int count) {
        List<String> result = new ArrayList<>();
        if (!CronExpression.isValidExpression(cronExpression)) {
            return result;
        }
        try {
            CronExpression cron = new CronExpression(cronExpression);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date next = new Date();
            for (int i = 0; i < count; i++) {
                next = cron.getNextValidTimeAfter(next);
                if (next == null) break;
                result.add(sdf.format(next));
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
