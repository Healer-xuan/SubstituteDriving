package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;

import java.util.List;

public interface OrderService {


    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    Integer getOrderStatus(Long orderId);


    /**
     * 查询司机新订单数据
     * @return
     */
    List<NewOrderDataVo> findNewOrderQueueData(Long driverID);


    /**
     * 司机抢单
     * @param orderId
     * @return
     */
    Boolean robNewOrder(Long driverId, Long orderId);

    /**
     * 查找司机端当前订单
     * @return
     */
    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderInfoVo getOrderInfo(Long driverId, Long orderId);
}
