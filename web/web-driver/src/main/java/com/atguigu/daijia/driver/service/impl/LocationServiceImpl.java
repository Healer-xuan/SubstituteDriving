package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationFeignClient locationFeignClient;

    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    /**
     * 更新司机的位置信息
     * @param updateDriverLocationForm
     * @return
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //获取司机的个性化设置
        Long driverId = updateDriverLocationForm.getDriverId();
        Result<DriverSet> driverSetResult = driverInfoFeignClient.getDriverSet(driverId);
        DriverSet driverSet = driverSetResult.getData();

        //判断司机是否开始接单，开始接单则更新司机位置信息
        if(driverSet.getServiceStatus() == 1){
            //司机开始接单，更新司机位置信息
            Result<Boolean> booleanResult = locationFeignClient.updateDriverLocation(updateDriverLocationForm);
            return booleanResult.getData();
        }else{
            //司机没有接单
            throw new GuiguException(ResultCodeEnum.NO_START_SERVICE);
        }
    }
}
