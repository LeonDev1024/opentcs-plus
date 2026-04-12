package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.LocationTypeDTO;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;

import java.util.List;

/**
 * 位置类型领域服务接口
 */
public interface LocationTypeRepository extends IService<LocationTypeEntity> {

    /**
     * 分页查询位置类型列表
     * @param locationType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationTypeEntity> selectPage(LocationTypeEntity locationType, PageQuery pageQuery);

    /**
     * 分页查询位置类型列表（DTO）
     * @param locationType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationTypeDTO> selectPageDTO(LocationTypeEntity locationType, PageQuery pageQuery);

    /**
     * 分页查询位置类型列表（DTO 查询条件）
     * @param locationType 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LocationTypeDTO> selectPageDTO(LocationTypeDTO locationType, PageQuery pageQuery);

    /**
     * 查询所有位置类型
     * @return 位置类型列表
     */
    List<LocationTypeEntity> selectAll();

    /**
     * 查询所有位置类型（DTO）
     * @return 位置类型列表
     */
    List<LocationTypeDTO> listDTO();

    /**
     * 根据ID查询位置类型
     * @param id 位置类型ID
     * @return 位置类型
     */
    LocationTypeEntity selectById(Long id);

    /**
     * 根据ID查询位置类型（DTO）
     * @param id 位置类型ID
     * @return 位置类型
     */
    LocationTypeDTO getByIdDTO(Long id);

    /**
     * 保存位置类型（DTO）
     * @param locationType 位置类型
     * @return 是否保存成功
     */
    boolean saveDTO(LocationTypeDTO locationType);

    /**
     * 更新位置类型（DTO）
     * @param locationType 位置类型
     * @return 是否更新成功
     */
    boolean updateByIdDTO(LocationTypeDTO locationType);
}
