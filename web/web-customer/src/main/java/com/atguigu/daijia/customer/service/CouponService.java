package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService  {


    /**
     * 查询未领取优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit);


    /**
     * 查询未使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit);


    /**
     * 查询已使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit);


    /**
     * 乘客领取优惠卷
     * @param customerId
     * @param couponId
     * @return
     */
    Boolean receive(Long customerId, Long couponId);


    /**
     * 获取未使用的最佳优惠卷信息
     * @param customerId
     * @param orderAmount
     * @return
     */
    List<AvailableCouponVo> findAvailableCoupon(Long customerId, BigDecimal orderAmount);
}
