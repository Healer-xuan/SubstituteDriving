package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.XuanLogin;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.MonitorService;
import com.atguigu.daijia.model.entity.order.OrderMonitorRecord;
import com.atguigu.daijia.model.form.order.OrderMonitorForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "监控接口管理")
@RestController
@RequestMapping(value="/monitor")
@SuppressWarnings({"unchecked", "rawtypes"})
public class MonitorController {

    @Autowired
    private MonitorService MonitorService;

    /**
     * 保存订单监控记录数据，把录音转换成文本放入到mongoDB中
     * @param file
     * @param orderMonitorForm
     * @return
     */
    @Operation(summary = "保存订单监控记录数据")
    @PostMapping("/upload")
    public Result<Boolean> upload(@RequestParam("file")MultipartFile file, OrderMonitorForm orderMonitorForm){
        return Result.ok(MonitorService.upload(file,orderMonitorForm));
    }
	

}

