package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.opentcs.map.domain.entity.FloorConnection;

/**
 * 跨楼层连接 Mapper 接口
 *
 * @author lyc
 */
@Mapper
public interface FloorConnectionMapper extends BaseMapper<FloorConnection> {

}