package org.opentcs.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.order.domain.entity.TransportOrder;
import org.opentcs.order.mapper.TransportOrderMapper;
import org.opentcs.order.service.TransportOrderService;
import org.springframework.stereotype.Service;

/**
 * 运输订单 Service 实现类
 */
@Service
public class TransportOrderServiceImpl extends ServiceImpl<TransportOrderMapper, TransportOrder> implements TransportOrderService {

}