package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PlantModelEntity;

import java.util.List;

/**
 * 地图模型领域服务接口
 */
public interface PlantModelDomainService extends IService<PlantModelEntity> {

    /**
     * 分页查询地图模型列表
     * @param plantModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PlantModelEntity> selectPagePlantModel(PlantModelEntity plantModel, PageQuery pageQuery);

    /**
     * 查询所有地图模型
     * @return 地图模型列表
     */
    List<PlantModelEntity> selectAll();

    /**
     * 根据ID查询地图模型
     * @param id 地图模型ID
     * @return 地图模型
     */
    PlantModelEntity selectById(Long id);

    /**
     * 根据名称查询地图模型
     * @param name 地图模型名称
     * @return 地图模型
     */
    PlantModelEntity selectByName(String name);

    /**
     * 创建地图模型
     * @param plantModel 地图模型
     * @return 是否创建成功
     */
    boolean createPlantModel(PlantModelEntity plantModel);

    /**
     * 创建版本
     * @param modelId 模型ID
     * @param versionName 版本名称
     * @return 新版本ID
     */
    Long createVersion(Long modelId, String versionName);

    /**
     * 获取版本历史
     * @param id 模型ID
     * @param pageQuery 分页参数
     * @return 版本历史
     */
    TableDataInfo<PlantModelEntity> getVersionHistory(Long id, PageQuery pageQuery);

    /**
     * 验证拓扑
     * @param id 模型ID
     * @return 验证结果
     */
    String validateTopology(Long id);

    /**
     * 复制地图
     * @param modelId 原始模型ID
     * @param newName 新名称
     * @return 新模型ID
     */
    Long copyMap(Long modelId, String newName);
}
