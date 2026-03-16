package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;

/**
 * 工厂模型 Service 接口
 */
public interface FactoryModelService extends IService<FactoryModelEntity> {

    /**
     * 创建工厂模型
     * @param factoryModel 工厂模型
     * @return 是否创建成功
     */
    boolean createFactoryModel(FactoryModelEntity factoryModel);

    /**
     * 分页查询工厂模型列表
     * @param factoryModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<FactoryModelEntity> selectPageFactoryModel(FactoryModelEntity factoryModel, PageQuery pageQuery);

    /**
     * 获取工厂模型详情（含导航地图列表）
     * @param id 工厂模型ID
     * @return 工厂模型详情
     */
    FactoryModelEntity getFactoryModelDetail(Long id);

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
