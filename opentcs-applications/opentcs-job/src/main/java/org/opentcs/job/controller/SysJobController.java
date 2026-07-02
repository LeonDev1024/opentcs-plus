package org.opentcs.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.job.domain.SysJob;
import org.opentcs.job.service.ISysJobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class SysJobController {

    private final ISysJobService jobService;

    @SaCheckPermission("monitor:job:list")
    @GetMapping("/list")
    public R<List<SysJob>> list(SysJob query) {
        return R.ok(jobService.listJobs(query));
    }

    @SaCheckPermission("monitor:job:list")
    @GetMapping("/{jobId}")
    public R<SysJob> getInfo(@PathVariable Long jobId) {
        return R.ok(jobService.getById(jobId));
    }

    @SaCheckPermission("monitor:job:list")
    @PostMapping
    public R<Void> add(@RequestBody SysJob job) throws Exception {
        if (!jobService.checkCronExpressionIsValid(job.getCronExpression())) {
            return R.fail("cron 表达式不合法");
        }
        jobService.addJob(job);
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @PutMapping
    public R<Void> edit(@RequestBody SysJob job) throws Exception {
        if (!jobService.checkCronExpressionIsValid(job.getCronExpression())) {
            return R.fail("cron 表达式不合法");
        }
        jobService.updateJob(job);
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @DeleteMapping("/{jobId}")
    public R<Void> remove(@PathVariable Long jobId) throws Exception {
        jobService.deleteJobById(jobId);
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysJob job) throws Exception {
        jobService.changeStatus(job);
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @PutMapping("/run")
    public R<Void> run(@RequestBody SysJob job) throws Exception {
        jobService.run(job);
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @GetMapping("/checkCron")
    public R<Boolean> checkCron(@RequestParam String cronExpression) {
        return R.ok(jobService.checkCronExpressionIsValid(cronExpression));
    }

    @SaCheckPermission("monitor:job:list")
    @GetMapping("/nextTriggerTime")
    public R<List<String>> nextTriggerTime(
            @RequestParam String cronExpression,
            @RequestParam(defaultValue = "3") int count) {
        return R.ok(jobService.nextTriggerTimes(cronExpression, count));
    }
}
