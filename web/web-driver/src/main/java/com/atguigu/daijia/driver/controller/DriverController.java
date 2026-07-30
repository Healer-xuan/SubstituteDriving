package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

