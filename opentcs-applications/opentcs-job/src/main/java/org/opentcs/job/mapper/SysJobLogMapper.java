package org.opentcs.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.job.domain.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
}
