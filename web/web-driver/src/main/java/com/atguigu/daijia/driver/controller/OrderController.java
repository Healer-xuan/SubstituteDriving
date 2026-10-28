package com.atguigu.daijia.driver.controller;

import com.alibaba.nacos.plugin.auth.constant.Constants;
import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.service.OrderService;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.order.StartDriveForm;
import com.atguigu.daijia.model.form.order.UpdateOrderCartForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {


    @Autowired
    private OrderService orderService;

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
     * 查询司机新订单数据
     * @return
     */
    @Operation(summary = "查询司机新订单数据")
    @XuanLogin
    @GetMapping("/findNewOrderQueueData")
    public Result<List<NewOrderDataVo>> findNewOrderQueueData(){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.findNewOrderQueueData(driverId));
    }


    /**
     * 查找司机端当前订单
     * @return
     */
    @Operation(summary = "查找司机端当前订单")
    @XuanLogin
    @GetMapping("/searchDriverCurrentOrder")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder(){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchDriverCurrentOrder(driverId));
    }


    /**
     * 司机抢单
     * @param orderId
     * @return
     */
    @Operation(summary = "司机抢单")
    @XuanLogin
    @GetMapping("/robNewOrder/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable("orderId") Long orderId){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.robNewOrder(driverId,orderId));
    }


    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Operation(summary = "获取订单信息")
    @XuanLogin
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable("orderId") Long orderId){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.getOrderInfo(driverId,orderId));
    }


    /**
     * 司机端司乘同显
     * @param calculateDrivingLineForm
     * @return
     */
    @Operation(summary = "司机端司乘同显(计算最佳驾驶路线)")
    @XuanLogin
    @PostMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm){
        return Result.ok(orderService.calculateDrivingLine(calculateDrivingLineForm));
    }


    /**
     * 司机到达起始点
     * @param orderId
     * @return
     */
    @Operation(summary = "司机到达起始地点")
    @XuanLogin
    @GetMapping("/driverArriveStartLocation/{orderId}")
    public Result<Boolean> driverArriveStartLocation(@PathVariable("orderId") Long orderId){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.driverArriveStartLocation(orderId,driverId));
    }


    /**
     * 司机更新代驾车辆信息
     * @param updateOrderCartForm
     * @return
     */
    @Operation(summary = "司机更新代驾车辆信息")
    @XuanLogin
    @PostMapping("/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm){
        Long driverId = AuthContextHolder.getUserId();
        updateOrderCartForm.setDriverId(driverId);
        return Result.ok(orderService.updateOrderCart(updateOrderCartForm));
    }


    /**
     * 开始代驾服务
     * @param startDriveForm
     * @return
     */
    @Operation(summary = "开始代驾服务")
    @XuanLogin
    @PostMapping("/startDrive")
    public Result<Boolean> StartDrive(@RequestBody StartDriveForm startDriveForm){
        Long driverId = AuthContextHolder.getUserId();
        startDriveForm.setDriverId(driverId);
        return Result.ok(orderService.StartDrive(startDriveForm));
    }
}

