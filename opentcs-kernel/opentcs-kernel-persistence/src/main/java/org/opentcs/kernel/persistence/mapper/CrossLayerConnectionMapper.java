package org.opentcs.kernel.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;

import java.util.List;

/**
 * 跨层连接 Mapper 接口
 */
public interface CrossLayerConnectionMapper extends BaseMapper<CrossLayerConnectionEntity> {

    /**
     * 分页查询跨层连接列表
     * @param page 分页参数
     * @param connection 查询条件
     * @return 分页结果
     */
    IPage<CrossLayerConnectionEntity> selectPageCrossLayerConnection(IPage<CrossLayerConnectionEntity> page,
                                                                      CrossLayerConnectionEntity connection);

    /**
     * 根据工厂模型ID查询所有跨层连接
     * @param factoryModelId 工厂模型ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectByFactoryModelId(@Param("factoryModelId") Long factoryModelId);

    /**
     * 根据源地图ID和目标地图ID查询跨层连接
     * @param sourceMapId 源地图ID
     * @param destMapId 目标地图ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectBySourceAndDest(@Param("sourceMapId") Long sourceMapId,
                                                           @Param("destMapId") Long destMapId);

    /**
     * 查询可用的跨层连接（电梯/传送带）
     * @param factoryModelId 工厂模型ID
     * @return 可用的跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectAvailableConnections(@Param("factoryModelId") Long factoryModelId);
}
