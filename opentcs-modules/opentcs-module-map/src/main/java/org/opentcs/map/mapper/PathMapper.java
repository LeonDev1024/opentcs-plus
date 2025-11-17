package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.opentcs.map.domain.entity.Path;

/**
 * 路径 Mapper 接口
 *
 * @author lyc
 */
@Mapper
public interface PathMapper extends BaseMapper<Path> {

}