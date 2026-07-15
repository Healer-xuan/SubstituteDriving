package com.atguigu.daijia.customer.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 定义远程调用接口
 */
@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

    /**
     * 微信小程序登录
     * @param code
     * @return
     */
    @GetMapping("/customer/info/login/{code}")
    public Result<Long> login(@PathVariable String code);


    /**
     * 获取用户登录信息
     * @param customerId
     * @return
     */
    @GetMapping("/customer/info/getCustomerInfo/{customerId}")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable("customerId") Long customerId);
}