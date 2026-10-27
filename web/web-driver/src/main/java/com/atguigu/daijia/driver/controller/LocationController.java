package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping(value="/location")
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * 更新司机的位置信息
     * @param updateDriverLocationForm
     * @return
     */
    @Operation(summary = "开启接单服务: 更新司机经纬度位置")
    @XuanLogin
    @PostMapping("/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm updateDriverLocationForm){
        Long driverId = AuthContextHolder.getUserId();
        updateDriverLocationForm.setDriverId(driverId);
        return Result.ok(locationService.updateDriverLocation(updateDriverLocationForm));
    }


    /**
     * 更新订单司机位置信息到Redis
     * @param updateOrderLocationForm
     * @return
     */
    @Operation(summary = "司机赶往代驾起始点：更新司机地 址到缓存")
    @XuanLogin
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm updateOrderLocationForm){
        return Result.ok(locationService.updateOrderLocationToCache(updateOrderLocationForm));
    }

}

