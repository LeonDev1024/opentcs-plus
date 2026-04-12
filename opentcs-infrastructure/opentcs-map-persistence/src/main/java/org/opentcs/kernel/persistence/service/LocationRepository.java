package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.persistence.entity.LocationEntity;

import java.util.List;

/**
 * 位置/站点领域服务接口
 * 放在 kernel-persistence 作为领域层接口
 */
public interface LocationRepository extends IService<LocationEntity> {

    /**
     * 分页查询位置列表
     * @param location 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationEntity> selectPage(LocationEntity location, PageQuery pageQuery);

    /**
     * 分页查询位置列表（DTO）
     * @param location 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationDTO> selectPageDTO(LocationEntity location, PageQuery pageQuery);

    /**
     * 分页查询位置列表（DTO 查询条件）
     * @param location 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationDTO> selectPageDTO(LocationDTO location, PageQuery pageQuery);

    /**
     * 保存位置（DTO）
     * @param location 位置数据
     * @return 是否保存成功
     */
    boolean saveDTO(LocationDTO location);

    /**
     * 根据导航地图ID查询位置列表
     * @param navigationMapId 导航地图ID
     * @return 位置列表
     */
    List<LocationEntity> selectByNavigationMapId(Long navigationMapId);

    /**
     * 根据导航地图ID查询位置列表（DTO）
     * @param navigationMapId 导航地图ID
     * @return 位置列表
     */
    List<LocationDTO> selectByNavigationMapIdDTO(Long navigationMapId);

    /**
     * 根据地图ID列表查询位置
     * @param mapIds 地图ID列表
     * @return 位置列表
     */
    List<LocationEntity> selectByMapIds(List<Long> mapIds);

    /**
     * 根据地图ID列表查询位置（DTO）
     * @param mapIds 地图ID列表
     * @return 位置列表
     */
    List<LocationDTO> selectByMapIdsDTO(List<Long> mapIds);

    /**
     * 根据ID查询位置详情
     * @param id 位置ID
     * @return 位置详情
     */
    LocationEntity selectById(Long id);

    /**
     * 根据ID查询位置详情（DTO）
     * @param id 位置ID
     * @return 位置详情
     */
    LocationDTO selectByIdDTO(Long id);

    /**
     * 根据ID更新位置（DTO）
     * @param location 位置数据
     * @return 是否更新成功
     */
    boolean updateByIdDTO(LocationDTO location);

    /**
     * 根据工厂模型ID查询所有位置（兼容旧版）
     * @param plantModelId 工厂模型ID
     * @return 位置列表
     */
    List<LocationEntity> selectAllLocationByPlantModelId(Long plantModelId);

    /**
     * 根据导航地图ID删除所有位置
     * @param navigationMapId 导航地图ID
     * @return 删除数量
     */
    int removeByMap(Long navigationMapId);
}
