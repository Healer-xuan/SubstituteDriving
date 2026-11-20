package com.atguigu.daijia.order.mapper;

import com.atguigu.daijia.model.entity.order.OrderBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface OrderBillMapper extends BaseMapper<OrderBill> {

    //更新订单优惠券金额
    void updateCouponAmount(Long orderId, BigDecimal couponAmount);
}
