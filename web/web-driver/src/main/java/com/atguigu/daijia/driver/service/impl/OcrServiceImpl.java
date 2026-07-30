package com.atguigu.daijia.driver.service.impl;

import com.alibaba.nacos.common.codec.Base64;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.OcrFeignClient;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.driver.service.OcrService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import com.atguigu.daijia.model.vo.driver.DriverLicenseOcrVo;
import com.atguigu.daijia.model.vo.driver.IdCardOcrVo;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OcrServiceImpl implements OcrService {

    @Autowired
    private OcrFeignClient ocrFeignClient;

    /**
     * 身份证识别
     * @param file
     * @return
     */
    @Override
    public IdCardOcrVo idCardOcr(MultipartFile file) {
        Result<IdCardOcrVo> idCardOcrVoResult = ocrFeignClient.idCardOcr(file);
        IdCardOcrVo idCardOcrVo = idCardOcrVoResult.getData();
        return idCardOcrVo;
    }


    /**
     * 驾驶证识别
     * @param file
     * @return
     */
    @Override
    public DriverLicenseOcrVo driverLicenseOcr(MultipartFile file) {
        Result<DriverLicenseOcrVo> driverLicenseOcrVoResult = ocrFeignClient.driverLicenseOcr(file);
        DriverLicenseOcrVo driverLicenseOcrVo = driverLicenseOcrVoResult.getData();
        return driverLicenseOcrVo;
    }
}
