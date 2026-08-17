package org.opentcs.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.job.domain.SysJobLog;
import org.opentcs.job.service.ISysJobLogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule/log")
@RequiredArgsConstructor
public class SysJobLogController {

    private final ISysJobLogService logService;

    @SaCheckPermission("monitor:job:list")
    @GetMapping("/list")
    public R<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            SysJobLog query) {
        Page<SysJobLog> page = new Page<>(pageNum, pageSize);
        return R.ok(logService.listLogs(page, query));
    }

    @SaCheckPermission("monitor:job:list")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logService.cleanAllLogs();
        return R.ok();
    }

    @SaCheckPermission("monitor:job:list")
    @DeleteMapping("/{logId}")
    public R<Void> remove(@PathVariable Long logId) {
        logService.deleteLogById(logId);
        return R.ok();
    }
}
