package com.atguigu.daijia.customer.controller;

import com.alibaba.nacos.plugin.auth.constant.Constants;
import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
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


    /**
     * 获取订单司机信息(下单成功后司乘同显中乘客看到的司机基本信息)
     * @param orderId
     * @return
     */
    @Operation(summary = "获取订单司机信息)")
    @XuanLogin
    @GetMapping("/getDriverInfo/{orderId}")
    public Result<DriverInfoVo> getDriverInfoOrder(@PathVariable Long orderId){
        Long customerId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getDriverInfoOrder(customerId,orderId));
    }


    /**
     * 乘客端获取司机经纬度位置
     * @param orderId
     * @return
     */
    @Operation(summary = "司机赶往代驾起始点：获取订单经纬度位置")
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long orderId){
        return Result.ok(orderService.getCacheOrderLocation(orderId));
    }


    /**
     * 乘客端司乘同显
     * @param calculateDrivingLineForm
     * @return
     */
    @Operation(summary = "乘客端司乘同显(计算最佳驾驶路线)")
    @XuanLogin
    @PostMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm){
        return Result.ok(orderService.calculateDrivingLine(calculateDrivingLineForm));
    }

}

