package org.opentcs.kernel.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;

import java.util.List;

/**
 * 导航地图 Mapper 接口
 */
public interface NavigationMapMapper extends BaseMapper<NavigationMapEntity> {

    /**
     * 分页查询导航地图列表
     * @param page 分页参数
     * @param navigationMap 查询条件
     * @return 分页结果
     */
    IPage<NavigationMapDTO> selectPageNavigationMap(IPage<NavigationMapDTO> page, NavigationMapEntity navigationMap);

    /**
     * 根据工厂模型ID查询所有导航地图
     * @param factoryModelId 工厂模型ID
     * @return 导航地图列表
     */
    List<NavigationMapDTO> selectByFactoryModelId(@Param("factoryModelId") Long factoryModelId);

    /**
     * 根据工厂模型ID和楼层号查询导航地图
     * @param factoryModelId 工厂模型ID
     * @param floorNumber 楼层号
     * @return 导航地图
     */
    NavigationMapDTO selectByFactoryModelIdAndFloor(@Param("factoryModelId") Long factoryModelId,
                                                   @Param("floorNumber") Integer floorNumber);

    /**
     * 根据地图编号查询导航地图
     * @param mapId 地图编号（如 map_001）
     * @return 导航地图
     */
    NavigationMapDTO selectByMapId(@Param("mapId") String mapId);
}
