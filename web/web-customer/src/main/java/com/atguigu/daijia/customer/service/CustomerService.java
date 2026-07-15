package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerService {

    //微信小程序登录
    String login(String code);

    //获取登录用户信息
    CustomerLoginVo getCustomerLoginInfo(String token);
}
