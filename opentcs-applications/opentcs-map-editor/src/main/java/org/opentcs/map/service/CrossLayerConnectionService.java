package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;

import java.util.List;

/**
 * 跨层连接 Service 接口
 */
public interface CrossLayerConnectionService extends IService<CrossLayerConnectionEntity> {

    /**
     * 创建跨层连接
     * @param connection 跨层连接
     * @return 是否创建成功
     */
    boolean createConnection(CrossLayerConnectionEntity connection);

    /**
     * 分页查询跨层连接列表
     * @param connection 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<CrossLayerConnectionEntity> selectPageConnection(CrossLayerConnectionEntity connection, PageQuery pageQuery);

    /**
     * 根据工厂模型ID获取所有跨层连接
     * @param factoryModelId 工厂模型ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectByFactoryModelId(Long factoryModelId);

    /**
     * 根据源地图ID和目标地图ID查询跨层连接
     * @param sourceMapId 源地图ID
     * @param destMapId 目标地图ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectBySourceAndDest(Long sourceMapId, Long destMapId);

    /**
     * 获取可用的跨层连接（电梯/传送带）
     * @param factoryModelId 工厂模型ID
     * @return 可用的跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectAvailableConnections(Long factoryModelId);

    /**
     * 预留电梯
     * @param connectionId 跨层连接ID
     * @param vehicleId 车辆ID
     * @return 是否预留成功
     */
    boolean reserveElevator(String connectionId, Long vehicleId);

    /**
     * 释放电梯
     * @param connectionId 跨层连接ID
     * @param vehicleId 车辆ID
     * @return 是否释放成功
     */
    boolean releaseElevator(String connectionId, Long vehicleId);

    /**
     * 更新跨层连接
     * @param connection 跨层连接
     * @return 是否更新成功
     */
    boolean updateConnection(CrossLayerConnectionEntity connection);

    /**
     * 删除跨层连接
     * @param id 跨层连接ID
     * @return 是否删除成功
     */
    boolean deleteConnection(Long id);
}
