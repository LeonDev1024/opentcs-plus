package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.opentcs.kernel.persistence.entity.PlantModelHistoryEntity;

@Mapper
public interface PlantModelHistoryMapper extends BaseMapper<PlantModelHistoryEntity> {
}

