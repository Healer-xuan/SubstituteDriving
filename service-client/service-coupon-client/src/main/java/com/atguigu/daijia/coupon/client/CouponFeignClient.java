package com.atguigu.daijia.coupon.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "service-coupon")
public interface CouponFeignClient {

    /**
     * 查询未领取优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/coupon/info/findNoReceivePage/{customerId}/{page}/{limit}")
    Result<PageVo<NoReceiveCouponVo>> findNoReceivePage(
            @PathVariable Long customerId,
            @PathVariable Long page,
            @PathVariable Long limit);


    /**
     * 查询未使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/coupon/info/findNoUsePage/{customerId}/{page}/{limit}")
    Result<PageVo<NoUseCouponVo>> findNoUsePage(
            @PathVariable Long customerId,
            @PathVariable Long page,
            @PathVariable Long limit);



    /**
     * 查询已使用优惠卷分页列表
     * @param customerId
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/coupon/info/findUsedPage/{customerId}/{page}/{limit}")
    Result<PageVo<UsedCouponVo>> findUsedPage(
            @PathVariable Long customerId,
            @PathVariable Long page,
            @PathVariable Long limit);



    /**
     * 乘客领取优惠卷
     * @param customerId
     * @param couponId
     * @return
     */
    @GetMapping("/coupon/info/receive/{customerId}/{couponId}")
    Result<Boolean> receive(@PathVariable Long customerId,@PathVariable Long couponId);
}