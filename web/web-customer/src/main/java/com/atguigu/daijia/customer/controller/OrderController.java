package com.atguigu.daijia.customer.controller;

import com.alibaba.nacos.plugin.auth.constant.Constants;
import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询乘客当前订单
     * @return
     */
    @Operation(summary = "查询乘客当前订单")
    @XuanLogin
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(){
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchCustomerCurrentOrder(customerId));
    }


    /**
     * 预估订单数据
     * @param expectOrderForm
     * @return
     */
    @Operation(summary = "预估订单数据")
    @XuanLogin
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm){
        return Result.ok(orderService.expectOrder(expectOrderForm));
    }


    /**
     * 乘客下单
     * @param submitOrderForm
     * @return
     */
    @Operation(summary = "乘客下单")
    @XuanLogin
    @PostMapping("/submitOrder")
    public Result<Long> submitOrder(@RequestBody SubmitOrderForm submitOrderForm){
        submitOrderForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(orderService.submitOrder(submitOrderForm));
    }


    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    @Operation(summary = "查询订单状态")
    @XuanLogin
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId){
        return Result.ok(orderService.getOrderStatus(orderId));
    }


    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Operation(summary = "获取订单信息")
    @XuanLogin
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable Long orderId){
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getOrderInfo(customerId,orderId));
    }

}

