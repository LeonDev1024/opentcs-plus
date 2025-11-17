package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.opentcs.map.domain.entity.Building;

/**
 * 建筑物 Mapper 接口
 *
 * @author lyc
 */
@Mapper
public interface BuildingMapper extends BaseMapper<Building> {

}