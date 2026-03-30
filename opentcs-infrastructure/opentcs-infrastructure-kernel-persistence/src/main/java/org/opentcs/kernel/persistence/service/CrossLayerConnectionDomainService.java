package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;

import java.util.List;

/**
 * 跨层连接领域服务接口
 */
public interface CrossLayerConnectionDomainService extends IService<CrossLayerConnectionEntity> {

    /**
     * 创建跨层连接
     * @param connection 跨层连接
     * @return 是否创建成功
     */
    boolean createConnection(CrossLayerConnectionEntity connection);

    /**
     * 分页查询跨层连接列表（兼容旧版）
     * @param connection 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<CrossLayerConnectionEntity> selectPageConnection(CrossLayerConnectionEntity connection, PageQuery pageQuery);

    /**
     * 分页查询跨层连接列表
     * @param connection 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<CrossLayerConnectionEntity> selectPage(CrossLayerConnectionEntity connection, PageQuery pageQuery);

    /**
     * 根据工厂模型ID查询跨层连接列表
     * @param factoryModelId 工厂模型ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectByFactoryModelId(Long factoryModelId);

    /**
     * 根据导航地图ID查询跨层连接列表
     * @param navigationMapId 导航地图ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectByNavigationMapId(Long navigationMapId);

    /**
     * 根据源地图和目标地图查询跨层连接
     * @param sourceMapId 源地图ID
     * @param destMapId 目标地图ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectBySourceAndDest(Long sourceMapId, Long destMapId);

    /**
     * 查询可用的跨层连接
     * @param factoryModelId 工厂模型ID
     * @return 跨层连接列表
     */
    List<CrossLayerConnectionEntity> selectAvailableConnections(Long factoryModelId);

    /**
     * 预留电梯
     * @param connectionId 连接ID
     * @param vehicleId 车辆ID
     * @return 是否成功
     */
    boolean reserveElevator(String connectionId, Long vehicleId);

    /**
     * 释放电梯
     * @param connectionId 连接ID
     * @param vehicleId 车辆ID
     * @return 是否成功
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

    /**
     * 根据ID查询跨层连接
     * @param id 跨层连接ID
     * @return 跨层连接
     */
    CrossLayerConnectionEntity selectById(Long id);
}
