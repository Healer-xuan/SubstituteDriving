package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.dispatch.client.NewOrderFeignClient;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;

    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    @Override
    public String login(String code) {
        //1.拿着code值远程调用获取司机id
        Result<Long> longResult = driverInfoFeignClient.login(code);

        //2.判断如果返回失败了，返回错误提示
        Integer resultCode = longResult.getCode();
        if(resultCode != 200){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //3.获取远程调用返回的用户id
        //3.1判断返回用户id是否为空，如果为空，返回错误提示
        Long driverId = longResult.getData();
        if(driverId == null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        //4.生成token字符串
        String token = UUID.randomUUID().toString().replaceAll("-", "");

        //5.把token和司机id放到redis，设置过期时间
        //key:token   value:driverId
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                driverId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);

        //6.返回token
        return token;
    }


    /**
     * 获取司机认证信息
     * @param driverId
     * @return
     */
    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        Result<DriverAuthInfoVo> authInfoVoResult = driverInfoFeignClient.getDriverAuthInfo(driverId);
        DriverAuthInfoVo driverAuthInfoVo = authInfoVoResult.getData();
        return driverAuthInfoVo;
    }

    /**
     * 修改司机认证信息
     * @param updateDriverAuthInfoForm
     * @return
     */
    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        Result<Boolean> booleanResult = driverInfoFeignClient.updateDriverAuthInfo(updateDriverAuthInfoForm);
        Boolean data = booleanResult.getData();
        return data;
    }


    /**
     * 创建司机人脸模型
     * @param driverFaceModelForm
     * @return
     */
    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        Result<Boolean> booleanResult = driverInfoFeignClient.creatDriverFaceModel(driverFaceModelForm);
        Boolean data = booleanResult.getData();
        return data;
    }

    /**
     * 判断司机当日是否进行人脸识别
     * @param driverId
     * @return
     */
    @Override
    public Boolean isFaceRecognition(Long driverId) {
        return driverInfoFeignClient.isFaceRecognition(driverId).getData();
    }

    /**
     * 验证司机人脸
     * @param driverFaceModelForm
     * @return
     */
    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.verifyDriverFace(driverFaceModelForm).getData();
    }

    /**
     * 开始接单服务
     * @param driverId
     * @return
     */
    @Override
    public Boolean startService(Long driverId) {
        //1.判断是否完成认证
        DriverLoginVo data = driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
        if(data.getAuthStatus() != 2){
            //说明没有认证
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }

        //2.判断是否完成当日人脸识别
        Boolean isFace = driverInfoFeignClient.isFaceRecognition(driverId).getData();
        if(!isFace){
            throw new GuiguException(ResultCodeEnum.FACE_ERROR);
        }

        //3.更新订单状态 1
        driverInfoFeignClient.updateServiceStatus(driverId,1);

        //4.删除redis司机位置信息
        locationFeignClient.removeDriverLocation(driverId);

        //5.清空司机临时队列数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }


    /**
     * 停止接单服务
     * @param driverId
     * @return
     */
    @Override
    public Boolean stopService(Long driverId) {
        //1.更新订单状态 0
        driverInfoFeignClient.updateServiceStatus(driverId,0);

        //2.删除司机位置信息
        locationFeignClient.removeDriverLocation(driverId);

        //3.清空司机临时队列数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);

        return true;
    }
}
