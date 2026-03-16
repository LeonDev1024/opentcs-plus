package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.opentcs.kernel.persistence.entity.ElevatorScheduleEntity;

import java.util.List;

/**
 * 电梯调度记录 Mapper 接口
 */
public interface ElevatorScheduleMapper extends BaseMapper<ElevatorScheduleEntity> {

    /**
     * 分页查询电梯调度记录
     * @param page 分页参数
     * @param schedule 查询条件
     * @return 分页结果
     */
    IPage<ElevatorScheduleEntity> selectPageElevatorSchedule(IPage<ElevatorScheduleEntity> page,
                                                               ElevatorScheduleEntity schedule);

    /**
     * 根据连接ID查询调度记录
     * @param connectionId 连接ID
     * @return 调度记录列表
     */
    List<ElevatorScheduleEntity> selectByConnectionId(@Param("connectionId") String connectionId);

    /**
     * 根据车辆ID查询调度记录
     * @param vehicleId 车辆ID
     * @return 调度记录列表
     */
    List<ElevatorScheduleEntity> selectByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * 查询待执行的调度记录
     * @return 待执行的调度记录列表
     */
    List<ElevatorScheduleEntity> selectPendingSchedules();
}
