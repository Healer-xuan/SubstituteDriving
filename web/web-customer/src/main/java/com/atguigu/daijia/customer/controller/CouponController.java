package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CouponService;
import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.base.PageVo;
import com.atguigu.daijia.model.vo.coupon.AvailableCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.NoUseCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@Tag(name = "优惠券活动接口管理")
@RestController
@RequestMapping(value="/coupon")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CouponController {

    @Autowired
    private CouponService couponService;


    /**
     * 查询未领取优惠卷分页列表
     * @param page
     * @param limit
     * @return
     */
    @Operation(summary = "查询未领取优惠卷分页列表")
    @XuanLogin
    @GetMapping("/findNoReceivePage/{page}/{limit}")
    public Result<PageVo<NoReceiveCouponVo>> findNoReceivePage(
            @Parameter(name = "page",description = "当前页码",required = true)
            @PathVariable Long page,
            @Parameter(name = "limit",description = "每页记录数",required = true)
            @PathVariable Long limit){


        Long customerId = AuthContextHolder.getUserId();
        PageVo<NoReceiveCouponVo> pageVo = couponService.findNoReceivePage(customerId,page,limit);
        return Result.ok(pageVo);
    }


    /**
     * 查询未使用优惠卷分页列表
     * @param page
     * @param limit
     * @return
     */
    @Operation(summary = "查询未使用优惠卷分页列表")
    @XuanLogin
    @GetMapping("/findNoUsePage/{page}/{limit}")
    public Result<PageVo<NoUseCouponVo>> findNoUsePage(
            @Parameter(name = "page",description = "当前页码",required = true)
            @PathVariable Long page,
            @Parameter(name = "limit",description = "每页记录数",required = true)
            @PathVariable Long limit){

        Long customerId = AuthContextHolder.getUserId();
        PageVo<NoUseCouponVo> pageVo = couponService.findNoUsePage(customerId,page,limit);
        return Result.ok(pageVo);
    }


    /**
     * 查询已使用优惠卷分页列表
     * @param page
     * @param limit
     * @return
     */
    @Operation(summary = "查询已使用优惠卷分页列表")
    @XuanLogin
    @GetMapping("/findUsedPage/{page}/{limit}")
    public Result<PageVo<UsedCouponVo>> findUsedPage(
            @Parameter(name = "page",description = "当前页码",required = true)
            @PathVariable Long page,
            @Parameter(name = "limit",description = "每页记录数",required = true)
            @PathVariable Long limit){

        Long customerId = AuthContextHolder.getUserId();
        PageVo<UsedCouponVo> pageVo = couponService.findUsedPage(customerId,page,limit);
        return Result.ok(pageVo);
    }


    /**
     * 乘客领取优惠卷
     * @param couponId
     * @return
     */
    @Operation(summary = "乘客领取优惠卷")
    @XuanLogin
    @GetMapping("/receive/{couponId}")
    public Result<Boolean> receive(@PathVariable Long couponId){
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(couponService.receive(customerId,couponId));
    }


    /**
     * 获取未使用的最佳优惠卷信息
     * @param orderAmount
     * @return
     */
    @Operation(summary = "获取未使用的最佳优惠卷信息")
    @XuanLogin
    @GetMapping("/findAvailableCoupon/{orderAmount}")
    public Result<List<AvailableCouponVo>> findAvailableCoupon(@PathVariable BigDecimal orderAmount){
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(couponService.findAvailableCoupon(customerId,orderAmount));
    }
}

