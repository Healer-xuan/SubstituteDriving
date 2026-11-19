package com.atguigu.daijia.coupon.service;

import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
