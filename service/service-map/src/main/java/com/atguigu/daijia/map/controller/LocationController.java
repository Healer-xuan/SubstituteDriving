package com.atguigu.daijia.map.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.map.service.MapService;
import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "位置API接口管理")
@RestController
@RequestMapping("/map/location")
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationController {


    @Autowired
    private LocationService locationService;
    @Autowired
    private MapService mapService;

    /**
     * 更新司机位置信息
     * @param updateDriverLocationForm
     * @return
     */
    @Operation(summary = "开启接单服务: 更新司机经纬度位置")
    @PostMapping("/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm updateDriverLocationForm){
        Boolean flag = locationService.updateDriverLocation(updateDriverLocationForm);
        return Result.ok(flag);
    }


    /**
     * 删除司机位置信息
     * @param driverId
     * @return
     */
    @Operation(summary = "关闭接单服务: 删除司机经纬度位置")
    @DeleteMapping("/removeDriverLocation{driverId}")
    public Result<Boolean> removeDriverLocation(@PathVariable Long driverId){
        return Result.ok(locationService.removeDriverLocation(driverId));
    }


    /**
     * 搜素附近满足条件的司机信息
     * @param searchNearByDriverForm
     * @return
     */
    @Operation(summary = "搜素附近满足条件的司机信息")
    @PostMapping("/searchNearByDriver")
    public Result<List<NearByDriverVo>> searchNearByDriver(@RequestBody SearchNearByDriverForm searchNearByDriverForm){
        return Result.ok(locationService.searchNearByDriver(searchNearByDriverForm));
    }


    /**
     * 更新订单司机位置信息到Redis
     * @param updateOrderLocationForm
     * @return
     */
    @Operation(summary = "司机赶往代驾起始点：更新司机地址到缓存")
    @PostMapping("/updateOrderLocationToCache")
    public Result<Boolean> updateOrderLocationToCache(@RequestBody UpdateOrderLocationForm updateOrderLocationForm){
        return Result.ok(locationService.updateOrderLocationToCache(updateOrderLocationForm));
    }


    /**
     * 乘客端获取司机经纬度位置
     * @param orderId
     * @return
     */
    @Operation(summary = "司机赶往代驾起始点：获取订单经纬度位置")
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long orderId){
        return Result.ok(locationService.getCacheOrderLocation(orderId));
    }


    /**
     * 批量保存代驾服务订单位置
     * @param orderLocationServiceFormList
     * @return
     */
    @Operation(summary = "批量保存代驾服务订单位置")
    @PostMapping("/saveOrderServiceLocation")
    public Result<Boolean> saveOrderServiceLocation(@RequestBody List<OrderServiceLocationForm> orderLocationServiceFormList){
        return Result.ok(locationService.saveOrderServiceLocation(orderLocationServiceFormList));
    }
}

