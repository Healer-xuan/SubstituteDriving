package com.atguigu.daijia.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.mapper.DriverAccountMapper;
import com.atguigu.daijia.driver.mapper.DriverInfoMapper;
import com.atguigu.daijia.driver.mapper.DriverLoginLogMapper;
import com.atguigu.daijia.driver.mapper.DriverSetMapper;
import com.atguigu.daijia.driver.service.CosService;
import com.atguigu.daijia.driver.service.DriverInfoService;
import com.atguigu.daijia.model.entity.driver.DriverAccount;
import com.atguigu.daijia.model.entity.driver.DriverInfo;
import com.atguigu.daijia.model.entity.driver.DriverLoginLog;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private CosService cosService;
    @Autowired
    private DriverInfoMapper driverInfoMapper;
    @Autowired
    private DriverSetMapper driverSetMapper;
    @Autowired
    private DriverAccountMapper driverAccountMapper;
    @Autowired
    private DriverLoginLogMapper driverLoginLogMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 微信小程序登录接口
     * @param code
     * @return
     */
    @Override
    public Long login(String code) {
        String openid = null;
        try {
            //1.获取到code值，使用微信工具包对象，获取微信唯一标识openid
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            openid = sessionInfo.getOpenid();


            //2.根据openid查询数据库表，判断是否第一次登录
            LambdaQueryWrapper<DriverInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DriverInfo::getWxOpenId,openid);
            DriverInfo driverInfo = driverInfoMapper.selectOne(wrapper);

            //3.如果第一次登录，添加司机基本信息
            if(driverInfo == null){
                driverInfo = new DriverInfo();
                driverInfo.setNickname(String.valueOf(System.currentTimeMillis()));
                driverInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
                driverInfo.setWxOpenId(openid);
                driverInfoMapper.insert(driverInfo);
            }

            //3.1 初始化司机相关设置(比如接单范围)
            DriverSet driverSet = new DriverSet();
            driverSet.setDriverId(driverInfo.getId());
            driverSet.setOrderDistance(new BigDecimal(0));//0：无限制
            driverSet.setAcceptDistance(new BigDecimal(SystemConstant.ACCEPT_DISTANCE));//默认接单范围：5公里
            driverSet.setIsAutoAccept(0);//0：否 1：是
            driverSetMapper.insert(driverSet);

            //3.2 初始化司机账户信息
            DriverAccount driverAccount = new DriverAccount();
            driverAccount.setDriverId(driverInfo.getId());
            driverAccountMapper.insert(driverAccount);


            //4.添加司机登录记录
            DriverLoginLog driverLoginLog = new DriverLoginLog();
            driverLoginLog.setDriverId(driverInfo.getId());
            driverLoginLog.setMsg("小程序登录");
            driverLoginLogMapper.insert(driverLoginLog);

            //5.返回司机id
            return driverInfo.getId();

        } catch (WxErrorException e) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }


    /**
     * 获取司机基本信息
     * @param driverId
     * @return
     */
    @Override
    public DriverLoginVo getDriverInfo(Long driverId) {
        //1.根据司机id查询司机基本信息
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);

        //2.将数据封装到VO对象中
        DriverLoginVo driverLoginVo = new DriverLoginVo();
        BeanUtils.copyProperties(driverInfo,driverLoginVo);

        //3.判断是否需要创建腾讯云人脸模型
        String faceModelId = driverInfo.getFaceModelId();
        boolean hasText = StringUtils.hasText(faceModelId);
        driverLoginVo.setIsArchiveFace(hasText);

        //4.返回封装的vo对象
        return driverLoginVo;
    }


    /**
     * 获取司机认证信息
     * @param driverId
     * @return
     */
    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
        DriverAuthInfoVo driverAuthInfoVo = new DriverAuthInfoVo();
        BeanUtils.copyProperties(driverInfo,driverAuthInfoVo);

        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImageUrl(driverInfo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImageUrl(driverInfo.getIdcardBackUrl()));
        driverAuthInfoVo.setIdcardHandShowUrl(cosService.getImageUrl(driverInfo.getIdcardHandUrl()));

        driverAuthInfoVo.setDriverLicenseFrontShowUrl(cosService.getImageUrl(driverInfo.getDriverLicenseFrontUrl()));
        driverAuthInfoVo.setDriverLicenseBackShowUrl(cosService.getImageUrl(driverInfo.getDriverLicenseBackUrl()));
        driverAuthInfoVo.setDriverLicenseHandShowUrl(cosService.getImageUrl(driverInfo.getDriverLicenseHandUrl()));

        return driverAuthInfoVo;
    }
}