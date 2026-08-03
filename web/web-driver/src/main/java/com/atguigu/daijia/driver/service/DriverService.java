package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;

public interface DriverService {
    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    String login(String code);


    /**
     * 获取司机认证信息
     * @param driverId
     */
    DriverAuthInfoVo getDriverAuthInfo(Long driverId);


    /**
     * 修改司机认证信息
     * @param updateDriverAuthInfoForm
     * @return
     */
    Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm);


    /**
     * 创建司机人脸模型
     * @param driverFaceModelForm
     * @return
     */
    Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm);
}
