package com.atguigu.daijia.map.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-map")
public interface LocationFeignClient {


    /**
     * 更新司机位置信息
     * @param updateDriverLocationForm
     * @return
     */
    @PostMapping("/map/location/updateDriverLocation")
    Result<Boolean> updateDriverLocation(@RequestBody UpdateDriverLocationForm updateDriverLocationForm);


    /**
     * 删除司机位置信息
     * @param driverId
     * @return
     */
    @DeleteMapping("/map/location/removeDriverLocation{driverId}")
    Result<Boolean> removeDriverLocation(@PathVariable("driverId") Long driverId);
}