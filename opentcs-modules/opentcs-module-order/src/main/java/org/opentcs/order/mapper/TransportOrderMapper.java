package org.opentcs.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.opentcs.order.domain.entity.TransportOrder;

/**
 * 运输订单 Mapper 接口
 */
public interface TransportOrderMapper extends BaseMapper<TransportOrder> {

    /**
     * 分页查询运输订单列表
     * @param page 分页参数
     * @param transportOrder 查询条件
     * @return 分页结果
     */
    com.baomidou.mybatisplus.core.metadata.IPage<TransportOrder> selectPageTransportOrder(@Param("page") com.baomidou.mybatisplus.core.metadata.IPage<TransportOrder> page, @Param("transportOrder") TransportOrder transportOrder);
}