package org.opentcs.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.order.domain.entity.TransportOrder;

/**
 * 运输订单 Mapper 接口
 */
public interface TransportOrderMapper extends BaseMapper<TransportOrder> {

    /**
     * 分页查询运输订单列表
     * @param transportOrder 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<TransportOrder> selectPageTransportOrder(TransportOrder transportOrder, PageQuery pageQuery);
}