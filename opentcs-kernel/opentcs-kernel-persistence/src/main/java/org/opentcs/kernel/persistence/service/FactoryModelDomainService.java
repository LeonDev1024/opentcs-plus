package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;

import java.util.List;

/**
 * 工厂模型领域服务接口
 */
public interface FactoryModelDomainService extends IService<FactoryModelEntity> {

    /**
     * 创建工厂模型
     * @param factoryModel 工厂模型
     * @return 是否创建成功
     */
    boolean createFactoryModel(FactoryModelEntity factoryModel);

    /**
     * 分页查询工厂模型列表（兼容旧版）
     * @param factoryModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<FactoryModelEntity> selectPageFactoryModel(FactoryModelEntity factoryModel, PageQuery pageQuery);

    /**
     * 分页查询工厂模型列表（DTO）
     * @param factoryModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<FactoryModelDTO> selectPageFactoryModelDTO(FactoryModelEntity factoryModel, PageQuery pageQuery);

    /**
     * 分页查询工厂模型列表
     * @param factoryModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<FactoryModelEntity> selectPage(FactoryModelEntity factoryModel, PageQuery pageQuery);

    /**
     * 查询所有工厂模型
     * @return 工厂模型列表
     */
    List<FactoryModelEntity> selectAll();

    /**
     * 根据ID查询工厂模型
     * @param id 工厂模型ID
     * @return 工厂模型
     */
    FactoryModelEntity selectById(Long id);

    /**
     * 根据名称查询工厂模型
     * @param name 工厂名称
     * @return 工厂模型
     */
    FactoryModelEntity selectByName(String name);

    /**
     * 获取工厂模型详情（含导航地图列表）
     * @param id 工厂模型ID
     * @return 工厂模型详情
     */
    FactoryModelEntity getFactoryModelDetail(Long id);

    /**
     * 获取工厂模型详情（含导航地图列表，DTO）
     * @param id 工厂模型ID
     * @return 工厂模型详情
     */
    FactoryModelDTO getFactoryModelDetailDTO(Long id);

    /**
     * 更新工厂模型
     * @param factoryModel 工厂模型
     * @return 是否更新成功
     */
    boolean updateFactoryModel(FactoryModelEntity factoryModel);

    /**
     * 删除工厂模型
     * @param id 工厂模型ID
     * @return 是否删除成功
     */
    boolean deleteFactoryModel(Long id);
}
