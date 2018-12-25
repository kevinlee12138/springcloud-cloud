package com.kevin.order.service.impl;

import com.kevin.order.dataobject.OrderMaster;
import com.kevin.order.dto.OrderDTO;
import com.kevin.order.enums.OrderStatusEnum;
import com.kevin.order.enums.PayStatusEnum;
import com.kevin.order.repository.OrderDetailRepository;
import com.kevin.order.repository.OrderMasterRepository;
import com.kevin.order.service.OrderService;
import com.kevin.order.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    /**
     * 生成订单
     * @param orderDTO
     * @return
     */
    @Override
    public OrderDTO create(OrderDTO orderDTO) {
        //TODO 查询商品信息(调用商品服务)
        //TODO 计算总价
        //TODO 扣库存(调取商品服务)

        //订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(KeyUtil.genUniqueKey());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(new BigDecimal(10));
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }
}
