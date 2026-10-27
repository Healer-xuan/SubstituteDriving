package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value="/driver/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoController {

    @Autowired
    private DriverInfoService driverInfoService;

    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    @Operation(summary = "小程序登录")
    @GetMapping("/login/{code}")
    public Result<Long> login(@PathVariable String code) {
        return Result.ok(driverInfoService.login(code));
    }


    /**
     * 获取司机登录信息
     * @param driverId
     * @return
     */
    @Operation(summary = "获取司机基本信息")
    @GetMapping("/getDriverInfo/{driverId}")
    public Result<DriverLoginVo> getDriverInfo(@PathVariable Long driverId) {
        DriverLoginVo driverLoginVo = driverInfoService.getDriverInfo(driverId);
        return Result.ok(driverLoginVo);
    }


    /**
     * 获取司机认证信息
     * @param driverId
     * @return
     */
    @Operation(summary = "获取司机认证信息")
    @GetMapping("/getDriverAuthInfo/{driverId}")
    public Result<DriverAuthInfoVo> getDriverAuthInfo(@PathVariable Long driverId){
        DriverAuthInfoVo driverAuthInfoVo = driverInfoService.getDriverAuthInfo(driverId);
        return Result.ok(driverAuthInfoVo);
    }


    /**
     * 修改司机认证信息
     * @param updateDriverAuthInfoForm
     * @return
     */
    @Operation(summary = "修改司机认证信息")
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> updateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm){
        Boolean isSuccess = driverInfoService.updateDriverAuthInfo(updateDriverAuthInfoForm);
        return Result.ok(isSuccess);
    }


    /**
     * 创建司机人脸模型
     * @param driverFaceModelForm
     * @return
     */
    @Operation(summary = "创建司机人脸模型")
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm){
        Boolean isSuccess = driverInfoService.creatDriverFaceModel(driverFaceModelForm);
        return Result.ok(isSuccess);
    }


    /**
     * 获取司机设置信息
     * @param driverId
     * @return
     */
    @Operation(summary = "获取司机设置信息")
    @GetMapping("/getDriverSet/{driverId}")
    public Result<DriverSet> getDriverSet(@PathVariable("driverId") Long driverId){
        return Result.ok(driverInfoService.getDriverSet(driverId));
    }

    /**
     * 判断司机当日是否进行人脸识别
     * @param driverId
     * @return
     */
    @Operation(summary = "判断司机当日是否进行人脸识别")
    @GetMapping("/isFaceRecognition/{driverId}")
    public Result<Boolean> isFaceRecognition(@PathVariable("driverId") Long driverId){
        return Result.ok(driverInfoService.isFaceRecognition(driverId));
    }


    /**
     * 验证司机人脸
     * @param driverFaceModelForm
     * @return
     */
    @Operation(summary = "验证司机人脸")
    @PostMapping("/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm){
        return Result.ok(driverInfoService.verifyDriverFace(driverFaceModelForm));
    }


    /**
     * 更新司机接单状态
     * @param driverId
     * @param status
     * @return
     */
    @Operation(summary = "更新司机接单状态")
    @GetMapping("/updateServiceStatus/{driverId}/{status}")
    public Result<Boolean> updateServiceStatus(@PathVariable("driverId") Long driverId,@PathVariable("status") Integer status){
        return Result.ok(driverInfoService.updateServiceStatus(driverId,status));
    }


    /**
     * 获取订单司机信息(下单成功后司乘同显中乘客看到的司机基本信息)
     * @param driverId
     * @return
     */
    @Operation(summary = "获取订单司机信息)")
    @GetMapping("/getDriverInfoOrder/{driverId}")
    public Result<DriverInfoVo> getDriverInfoOrder(@PathVariable Long driverId){
        return Result.ok(driverInfoService.getDriverInfoOrder(driverId));
    }
}

