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
     */
    IPage<FactoryModelEntity> selectPageFactoryModel(IPage<FactoryModelEntity> page, FactoryModelEntity factoryModel);

    /**
     * 查询 C 前缀场景编号的最大序号（含已删除记录，保证唯一约束）
     */
    Integer selectMaxSceneCodeNumber();
}
