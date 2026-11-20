package com.atguigu.daijia.coupon.service;

import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.form.coupon.UseCouponForm;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface CouponInfoService extends IService<CouponInfo> {


    /**
     * 查询未领取优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId);


    /**
     * 查询未使用优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    PageVo<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId);


    /**
     * 查询已使用优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId);


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


    /**
     * 使用优惠卷
     * @param useCouponForm
     * @return
     */
    BigDecimal useCoupon(UseCouponForm useCouponForm);

}
