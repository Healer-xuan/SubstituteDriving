package com.atguigu.daijia.coupon.mapper;

import com.atguigu.daijia.model.entity.coupon.CouponInfo;
import com.atguigu.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.atguigu.daijia.model.vo.coupon.UsedCouponVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    /**
     * 查询未领取优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    IPage<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, @Param("customerId") Long customerId);


    /**
     * 查询未使用优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    IPage<NoReceiveCouponVo> findNoUsePage(Page<CouponInfo> pageParam, @Param("customerId") Long customerId);


    /**
     * 查询已使用优惠卷分页列表
     * @param pageParam
     * @param customerId
     * @return
     */
    IPage<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId);

    //更新领取数量
    int updateReceiveCount(Long couponId);

}
