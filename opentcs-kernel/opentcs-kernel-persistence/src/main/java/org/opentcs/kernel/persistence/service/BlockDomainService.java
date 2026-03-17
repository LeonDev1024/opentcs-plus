package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.BlockEntity;

import java.util.List;

/**
 * 区域/区块领域服务接口
 * 放在 kernel-persistence 作为领域层接口
 */
public interface BlockDomainService extends IService<BlockEntity> {

    /**
     * 分页查询区域列表
     * @param block 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<BlockEntity> selectPage(BlockEntity block, PageQuery pageQuery);

    /**
     * 根据工厂模型ID查询区域列表
     * @param factoryModelId 工厂模型ID
     * @return 区域列表
     */
    List<BlockEntity> selectByFactoryModelId(Long factoryModelId);

    /**
     * 根据工厂模型ID和类型查询区域列表
     * @param factoryModelId 工厂模型ID
     * @param type 区域类型
     * @return 区域列表
     */
    List<BlockEntity> selectByFactoryModelIdAndType(Long factoryModelId, String type);

    /**
     * 根据导航地图ID查询区域列表
     * @param navigationMapId 导航地图ID
     * @return 区域列表
     */
    List<BlockEntity> selectByNavigationMapId(Long navigationMapId);

    /**
     * 根据ID查询区域详情
     * @param id 区域ID
     * @return 区域详情
     */
    BlockEntity selectById(Long id);

    /**
     * 创建区域
     * @param block 区域
     * @return 是否创建成功
     */
    boolean create(BlockEntity block);

    /**
     * 更新区域
     * @param block 区域
     * @return 是否更新成功
     */
    boolean update(BlockEntity block);

    /**
     * 删除区域
     * @param id 区域ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 根据工厂模型ID查询所有区域（兼容旧版）
     * @param plantModelId 工厂模型ID
     * @return 区域列表
     */
    List<BlockEntity> selectAllBlockByPlantModelId(Long plantModelId);
}
