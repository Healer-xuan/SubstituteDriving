package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.coupon.client.CouponFeignClient;
import com.atguigu.daijia.customer.service.CouponService;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponFeignClient couponFeignClient;


    /**
     * 查询未领取优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoReceivePage(customerId,page,limit).getData();
    }


    /**
     * 查询未使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findNoUsePage(customerId,page,limit).getData();
    }


    /**
     * 查询已使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageVo<UsedCouponVo> findUsedPage(Long customerId, Long page, Long limit) {
        return couponFeignClient.findUsedPage(customerId,page,limit).getData();
    }


    /**
     * 乘客领取优惠卷
     * @param customerId
     * @param couponId
     * @return
     */
    @Override
    public Boolean receive(Long customerId, Long couponId) {
        return couponFeignClient.receive(customerId,couponId).getData();
    }



    /**
     * 获取未使用的最佳优惠卷信息
     * @param customerId
     * @param orderAmount
     * @return
     */
    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, BigDecimal orderAmount) {
        return couponFeignClient.findAvailableCoupon(customerId,orderAmount).getData();
    }
}
