package org.opentcs.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.job.domain.SysJob;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
}
