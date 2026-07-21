package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

    @Autowired
    private CustomerService customerInfoService;


    /**
     * 微信小程序登录
     * @param code
     * @return
     */
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> wxLogin(@PathVariable String code){
        return Result.ok(customerInfoService.login(code));
    }


    /**
     * 获取登录用户信息
     * @return
     */
    @Operation(summary = "获取用户登录信息")
    @XuanLogin
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(){

        //从ThreadLocal获取用户id
        Long customerId = AuthContextHolder.getUserId();

        //调用service
        CustomerLoginVo customerLoginVo = customerInfoService.getCustomerLoginInfo2(customerId);

        return Result.ok(customerLoginVo);
    }


    /**
     * 更新用户微信手机号
     * @param updateWxPhoneForm
     * @return
     */
    @Operation(summary = "更新用户微信手机号")
    @XuanLogin
    @PostMapping("/updateWxPhone")
    public Result updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
        updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
        return Result.ok(true);
    }


    /*
    @Operation(summary = "获取用户登录信息")
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token){
        //1. 从请求头里获取token
        //两种方式: 1.参数设置HttpServletRequest request
        //           String token = request.getHeader("token");
        //         2.参数用注解@RequestHeader(value = "token") String token

        //调用service
        CustomerLoginVo customerLoginVo = customerInfoService.getCustomerLoginInfo(token);

        return Result.ok(customerLoginVo);
    }
     */
}

