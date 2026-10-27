package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.service.OrderService;
import com.atguigu.daijia.dispatch.client.NewOrderFeignClient;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.map.client.MapFeignClient;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.customer.ExpectOrderForm;
import com.atguigu.daijia.model.form.customer.SubmitOrderForm;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.model.form.rules.FeeRuleRequestForm;
import com.atguigu.daijia.model.vo.customer.ExpectOrderVo;
import com.atguigu.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.atguigu.daijia.model.vo.driver.DriverInfoVo;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import com.atguigu.daijia.model.vo.rules.FeeRuleResponseVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.atguigu.daijia.rules.client.FeeRuleFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MapFeignClient mapFeignClient;
    @Autowired
    private FeeRuleFeignClient feeRuleFeignClient;
    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private LocationFeignClient locationFeignClient;

    /**
     * 预估订单
     * @param expectOrderForm
     * @return
     */
    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm expectOrderForm) {
        //1.获取驾驶线路
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(expectOrderForm,calculateDrivingLineForm);
        Result<DrivingLineVo> drivingLineVoResult = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = drivingLineVoResult.getData();

        //2.获取订单费用
        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);
        Result<FeeRuleResponseVo> feeRuleResponseVoResult = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleResponseVoResult.getData();

        //3.封装到ExpectOrderVo返回
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);
        return expectOrderVo;
    }


    /**
     * 乘客下单
     * @param submitOrderForm
     * @return
     */
       @Override
    public Long submitOrder(SubmitOrderForm submitOrderForm) {
        //重新计算驾驶路线
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(submitOrderForm,calculateDrivingLineForm);
        Result<DrivingLineVo> drivingLineVoResult = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = drivingLineVoResult.getData();

        //重新计算驾驶费用
        FeeRuleRequestForm calculateOrderFeeForm = new FeeRuleRequestForm();
        calculateOrderFeeForm.setDistance(drivingLineVo.getDistance());
        calculateOrderFeeForm.setStartTime(new Date());
        calculateOrderFeeForm.setWaitMinute(0);
        Result<FeeRuleResponseVo> feeRuleResponseVoResult = feeRuleFeignClient.calculateOrderFee(calculateOrderFeeForm);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleResponseVoResult.getData();

        //封装数据
        OrderInfoForm orderInfoForm = new OrderInfoForm();
        BeanUtils.copyProperties(submitOrderForm,orderInfoForm);
        orderInfoForm.setExpectDistance(drivingLineVo.getDistance());
        orderInfoForm.setExpectAmount(feeRuleResponseVo.getTotalAmount());
        Result<Long> longResult = orderInfoFeignClient.saveOrderInfo(orderInfoForm);
        Long orderId = longResult.getData();

        //任务调度: 查询附近可以接单司机(远程调用)
           NewOrderTaskVo newOrderTaskVo = new NewOrderTaskVo();
           newOrderTaskVo.setOrderId(orderId);
           newOrderTaskVo.setStartLocation(orderInfoForm.getStartLocation());
           newOrderTaskVo.setStartPointLongitude(orderInfoForm.getStartPointLongitude());
           newOrderTaskVo.setStartPointLatitude(orderInfoForm.getStartPointLatitude());
           newOrderTaskVo.setEndLocation(orderInfoForm.getEndLocation());
           newOrderTaskVo.setEndPointLongitude(orderInfoForm.getEndPointLongitude());
           newOrderTaskVo.setEndPointLatitude(orderInfoForm.getEndPointLatitude());
           newOrderTaskVo.setExpectAmount(orderInfoForm.getExpectAmount());
           newOrderTaskVo.setExpectDistance(orderInfoForm.getExpectDistance());
           newOrderTaskVo.setExpectTime(drivingLineVo.getDuration());
           newOrderTaskVo.setFavourFee(orderInfoForm.getFavourFee());
           newOrderTaskVo.setCreateTime(new Date());
           //远程调用
           Long jobId = newOrderFeignClient.addAndStartTask(newOrderTaskVo).getData();

           return orderId;
    }

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    @Override
    public Integer getOrderStatus(Long orderId) {
        Result<Integer> orderStatus = orderInfoFeignClient.getOrderStatus(orderId);
        return orderStatus.getData();
    }


    /**
     * 乘客端查找当前订单
     * @param customerId
     * @return
     */
    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        return  orderInfoFeignClient.searchCustomerCurrentOrder(customerId).getData();
    }


    /**
     * 获取订单信息
     * @param customerId
     * @param orderId
     * @return
     */
    @Override
    public OrderInfoVo getOrderInfo(Long customerId, Long orderId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        //判断一下customerId
        if(customerId != orderInfo.getCustomerId()){
            throw new GuiguException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo,orderInfoVo);
        orderInfoVo.setOrderId(orderId);
        return orderInfoVo;
    }


    /**
     * 获取订单司机信息(下单成功后司乘同显中乘客看到的司机基本信息)
     * @param orderId
     * @return
     */
    @Override
    public DriverInfoVo getDriverInfoOrder(Long customerId, Long orderId) {
        //查询订单，拿到司机id
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        Long driverId = orderInfo.getDriverId();
        if(orderInfo.getCustomerId() != customerId){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }


        return  driverInfoFeignClient.getDriverInfoOrder(driverId).getData();
    }


    /**
     * 乘客端获取司机经纬度位置
     * @param orderId
     * @return
     */
    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        return locationFeignClient.getCacheOrderLocation(orderId).getData();
    }


    /**
     * 乘客端司乘同显
     * @param calculateDrivingLineForm
     * @return
     */
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        return mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
    }
}
