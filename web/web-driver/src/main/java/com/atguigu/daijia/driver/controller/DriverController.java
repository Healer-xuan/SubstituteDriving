package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value="/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverController {

    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    @Operation(summary = "小程序登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(driverService.login(code));
    }


    /**
     * 获取司机登录信息
     * @return
     */
    @Operation(summary = "获取司机登录信息")
    @XuanLogin
    @GetMapping("/getDriverLoginInfo")
    public Result<DriverLoginVo> getDriverLoginInfo() {
        //1.获取用户id
        Long driverId = AuthContextHolder.getUserId();
        //2.远程调用，根据司机id获取对应相关信息
        Result<DriverLoginVo> driverLoginInfo = driverInfoFeignClient.getDriverLoginInfo(driverId);
        DriverLoginVo driverLoginVo = driverLoginInfo.getData();

        return Result.ok(driverLoginVo);
    }


    /**
     * 获取司机认证信息
     * @return
     */
    @Operation(summary = "获取司机认证信息")
    @XuanLogin
    @GetMapping("/getDriverAuthInfo")
    public Result<DriverAuthInfoVo> getDriverAuthInfo() {
        Long driverId = AuthContextHolder.getUserId();
        DriverAuthInfoVo driverAuthInfo = driverService.getDriverAuthInfo(driverId);
        return Result.ok(driverAuthInfo);
    }

    /**
     * 修改司机认证信息
     * @param updateDriverAuthInfoForm
     * @return
     */
    @Operation(summary = "修改司机认证信息")
    @XuanLogin
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> updateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        updateDriverAuthInfoForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(driverService.updateDriverAuthInfo(updateDriverAuthInfoForm));
    }


    /**
     * 创建司机人脸模型
     * @param driverFaceModelForm
     * @return
     */
    @Operation(summary = "创建司机人脸模型")
    @XuanLogin
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> creatDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm) {
        driverFaceModelForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(driverService.creatDriverFaceModel(driverFaceModelForm));
    }


    /**
     * 判断司机当日是否进行人脸识别
     * @return
     */
    @Operation(summary = "判断司机当日是否进行人脸识别")
    @XuanLogin
    @GetMapping("/isFaceRecognition")
    public Result<Boolean> isFaceRecognition(){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.isFaceRecognition(driverId));
    }


    /**
     * 验证司机人脸
     * @param driverFaceModelForm
     * @return
     */
    @Operation(summary = "验证司机人脸")
    @XuanLogin
    @PostMapping("/verifyDriverFace")
    public Result<Boolean> verifyDriverFace(@RequestBody DriverFaceModelForm driverFaceModelForm){
        driverFaceModelForm.setDriverId(AuthContextHolder.getUserId());
        return Result.ok(driverService.verifyDriverFace(driverFaceModelForm));
    }


    /**
     * 开始接单服务
     * @return
     */
    @Operation(summary = "开始接单服务")
    @XuanLogin
    @GetMapping("/startService")
    public Result<Boolean> startService(){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.startService(driverId));
    }


    /**
     * 停止接单服务
     * @return
     */
    @Operation(summary = "停止接单服务")
    @XuanLogin
    @GetMapping("/stopService")
    public Result<Boolean> stopService(){
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(driverService.stopService(driverId));
    }
}

