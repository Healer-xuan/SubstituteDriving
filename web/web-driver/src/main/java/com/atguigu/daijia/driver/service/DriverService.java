package com.atguigu.daijia.driver.service;

public interface DriverService {
    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    String login(String code);
}
