package org.opentcs.kernel.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;

/**
 * 工厂模型 Mapper 接口
 */
public interface FactoryModelMapper extends BaseMapper<FactoryModelEntity> {

    /**
     * 分页查询工厂模型列表
     * @param page 分页参数
     * @param factoryModel 查询条件
     * @return 分页结果
     */
    IPage<FactoryModelEntity> selectPageFactoryModel(IPage<FactoryModelEntity> page, FactoryModelEntity factoryModel);
}
