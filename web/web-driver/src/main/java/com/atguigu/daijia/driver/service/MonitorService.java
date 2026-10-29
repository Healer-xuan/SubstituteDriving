package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.entity.order.OrderMonitorRecord;
import com.atguigu.daijia.model.form.order.OrderMonitorForm;
import org.springframework.web.multipart.MultipartFile;

public interface MonitorService {

    /**
     * 保存订单监控记录数据
     * @param file
     * @param orderMonitorForm
     * @return
     */
    Boolean upload(MultipartFile file, OrderMonitorForm orderMonitorForm);
}
