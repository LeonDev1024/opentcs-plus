package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.persistence.entity.PointEntity;

import java.util.List;

/**
 * 点位领域服务接口
 * 放在 kernel-persistence 作为领域层接口
 */
public interface PointRepository extends IService<PointEntity> {

    /**
     * 分页查询点位列表
     * @param point 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery);

    /**
     * 保存点位（DTO）
     * @param point 点位数据
     * @return 是否保存成功
     */
    boolean saveDTO(PointDTO point);


    /**
     * 根据导航地图ID查询所有点位
     * @param navigationMapId 导航地图ID
     * @return 点位列表
     */
    List<PointEntity> listByMap(Long navigationMapId);

    /**
     * 根据导航地图ID查询所有点位（DTO）
     * @param navigationMapId 导航地图ID
     * @return 点位列表
     */
    List<PointDTO> listByMapDTO(Long navigationMapId);

    /**
     * 根据地图ID列表查询点位
     * @param mapIds 地图ID列表
     * @return 点位列表
     */
    List<PointEntity> listByMapIds(List<Long> mapIds);

    /**
     * 根据导航地图ID删除所有点位
     * @param navigationMapId 导航地图ID
     * @return 删除数量
     */
    int removeByMap(Long navigationMapId);

    /**
     * 根据ID更新点位（DTO）
     * @param point 点位数据
     * @return 是否更新成功
     */
    boolean updateByIdDTO(PointDTO point);
}
