package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传车内录音文件
     * @param file
     * @return
     */
    String upload(MultipartFile file);

}
