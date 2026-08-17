package org.opentcs.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.opentcs.job.domain.SysJobLog;

public interface ISysJobLogService {
    IPage<SysJobLog> listLogs(Page<SysJobLog> page, SysJobLog query);
    void addLog(SysJobLog log);
    void cleanAllLogs();
    boolean deleteLogById(Long logId);
}
