package org.opentcs.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.opentcs.job.domain.SysJobLog;
import org.opentcs.job.mapper.SysJobLogMapper;
import org.opentcs.job.service.ISysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl implements ISysJobLogService {

    private final SysJobLogMapper logMapper;

    @Override
    public IPage<SysJobLog> listLogs(Page<SysJobLog> page, SysJobLog query) {
        LambdaQueryWrapper<SysJobLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getJobName())) {
            wrapper.like(SysJobLog::getJobName, query.getJobName());
        }
        if (StringUtils.hasText(query.getJobGroup())) {
            wrapper.eq(SysJobLog::getJobGroup, query.getJobGroup());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysJobLog::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysJobLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    @Override
    public void addLog(SysJobLog log) {
        logMapper.insert(log);
    }

    @Override
    public void cleanAllLogs() {
        logMapper.delete(null);
    }

    @Override
    public boolean deleteLogById(Long logId) {
        return logMapper.deleteById(logId) > 0;
    }
}
