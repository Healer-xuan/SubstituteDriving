package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DriverInfoService extends IService<DriverInfo> {

    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    Long login(String code);

    /**
     * 获取司机基本信息
     * @param driverId
     */
    DriverLoginVo getDriverInfo(Long driverId);


    /**
     * 获取司机认证信息
     * @param driverId
     * @return
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


    /**
     * 获取司机设置信息
     * @param driverId
     * @return
     */
    DriverSet getDriverSet(Long driverId);

    /**
     * 判断司机当日是否人脸识别
     * @param driverId
     * @return
     */
    Boolean isFaceRecognition(Long driverId);

    /**
     * 验证司机人脸
     * @param driverFaceModelForm
     * @return
     */
    Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm);


    /**
     * 更新司机接单状态
     * @param driverId
     * @param status
     * @return
     */
    Boolean updateServiceStatus(Long driverId, Integer status);
}
