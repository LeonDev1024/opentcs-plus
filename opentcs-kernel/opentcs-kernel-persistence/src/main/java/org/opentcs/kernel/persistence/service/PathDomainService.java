package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.persistence.entity.PathEntity;

import java.util.List;

/**
 * 路径领域服务接口
 * 放在 kernel-persistence 作为领域层接口
 */
public interface PathDomainService extends IService<PathEntity> {

    /**
     * 分页查询路径列表
     * @param path 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PathEntity> selectPagePath(PathEntity path, PageQuery pageQuery);

    /**
     * 分页查询路径列表（DTO）
     * @param path 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PathDTO> selectPageDTO(PathEntity path, PageQuery pageQuery);

    /**
     * 根据导航地图ID查询所有路径
     * @param navigationMapId 导航地图ID
     * @return 路径列表
     */
    List<PathEntity> listByMap(Long navigationMapId);

    /**
     * 根据导航地图ID查询所有路径（DTO）
     * @param navigationMapId 导航地图ID
     * @return 路径列表
     */
    List<PathDTO> listByMapDTO(Long navigationMapId);

    /**
     * 根据地图ID列表查询路径
     * @param mapIds 地图ID列表
     * @return 路径列表
     */
    List<PathEntity> listByMapIds(List<Long> mapIds);

    /**
     * 根据地图ID列表查询路径（DTO）
     * @param mapIds 地图ID列表
     * @return 路径列表
     */
    List<PathDTO> listByMapIdsDTO(List<Long> mapIds);

    /**
     * 根据ID查询路径详情（DTO）
     * @param id 路径ID
     * @return 路径详情
     */
    PathDTO getByIdDTO(Long id);

    /**
     * 根据导航地图ID删除所有路径
     * @param navigationMapId 导航地图ID
     * @return 删除数量
     */
    int removeByMap(Long navigationMapId);
}
