package com.kevin.order.service.impl;

import com.kevin.order.client.ProductClient;
import com.kevin.order.dataobject.DecreaseStockInput;
import com.kevin.order.dataobject.OrderDetail;
import com.kevin.order.dataobject.OrderMaster;
import com.kevin.order.dataobject.ProductInfo;
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
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private ProductClient productClient;
    /**
     * 生成订单
     * @param orderDTO
     * @return
     */
    @Override
    public OrderDTO create(OrderDTO orderDTO) {
        //查询商品信息(调用商品服务)
        String orderId = KeyUtil.genUniqueKey();
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());
        List<ProductInfo> productInfoList = productClient.listForOrder(productIdList);
        //计算总价
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        for(OrderDetail orderDetail:orderDTO.getOrderDetailList()){
            for (ProductInfo productInfo:productInfoList){
                if (productInfo.getProductId().equals(orderDetail.getProductId())){
                    orderAmount = productInfo.getProductPrice().
                            multiply(new BigDecimal(orderDetail.getProductQuantity()))
                            .add(orderAmount);
                    BeanUtils.copyProperties(productInfo,orderDetail);
                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(KeyUtil.genUniqueKey());
                    orderDetailRepository.save(orderDetail);
                }
            }
        }
        //扣库存(调取商品服务)
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e ->new DecreaseStockInput(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockInputList);
        //订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }
}
