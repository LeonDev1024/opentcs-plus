package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;

import java.util.List;

/**
 * 导航地图 Service 接口
 */
public interface NavigationMapService extends IService<NavigationMapEntity> {

    /**
     * 创建导航地图
     * @param navigationMap 导航地图
     * @return 是否创建成功
     */
    boolean createNavigationMap(NavigationMapEntity navigationMap);

    /**
     * 分页查询导航地图列表
     * @param navigationMap 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<NavigationMapEntity> selectPageNavigationMap(NavigationMapEntity navigationMap, PageQuery pageQuery);

    /**
     * 根据工厂模型ID获取所有导航地图
     * @param factoryModelId 工厂模型ID
     * @return 导航地图列表
     */
    List<NavigationMapEntity> selectByFactoryModelId(Long factoryModelId);

    /**
     * 根据工厂模型ID和楼层号获取导航地图
     * @param factoryModelId 工厂模型ID
     * @param floorNumber 楼层号
     * @return 导航地图
     */
    NavigationMapEntity selectByFactoryModelIdAndFloor(Long factoryModelId, Integer floorNumber);

    /**
     * 获取导航地图详情（含点、路径）
     * @param id 导航地图ID
     * @return 导航地图详情
     */
    NavigationMapEntity getNavigationMapDetail(Long id);

    /**
     * 更新导航地图
     * @param navigationMap 导航地图
     * @return 是否更新成功
     */
    boolean updateNavigationMap(NavigationMapEntity navigationMap);

    /**
     * 删除导航地图
     * @param id 导航地图ID
     * @return 是否删除成功
     */
    boolean deleteNavigationMap(Long id);
}
