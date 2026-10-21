package com.atguigu.daijia.order.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.entity.order.OrderStatusLog;
import com.atguigu.daijia.model.enums.OrderStatus;
import com.atguigu.daijia.model.form.order.OrderInfoForm;
import com.atguigu.daijia.order.mapper.OrderInfoMapper;
import com.atguigu.daijia.order.mapper.OrderStatusLogMapper;
import com.atguigu.daijia.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 保存订单信息(乘客下单)
     * @param orderInfoForm
     * @return
     */
    @Override
    public Long saveOrderInfo(OrderInfoForm orderInfoForm) {
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoForm, orderInfo);

        //通过UUID生成随机的订单号
        String OrderNo = UUID.randomUUID().toString().replaceAll("-","");
        orderInfo.setOrderNo(OrderNo);
        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());
        orderInfoMapper.insert(orderInfo);

        //记录日志
        this.log(orderInfo.getId(),orderInfo.getStatus());


        //在redis中添加标识
        redisTemplate.opsForValue().set(RedisConstant.ORDER_ACCEPT_MARK,"0",RedisConstant.ORDER_ACCEPT_MARK_EXPIRES_TIME);

        return orderInfo.getId();
    }


    public void log(Long orderId, Integer status) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(orderId);
        orderStatusLog.setOrderStatus(status);
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog);
    }


    /**
     * 根据订单id获取订单状态
     * @param orderId
     * @return
     */
    @Override
    public Integer getOrderStatus(Long orderId) {
        //sql语句：select status from order_info where id=?
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getId,orderId);//id=?
        wrapper.select(OrderInfo::getStatus);//查询订单状态
        //调用wrapper方法
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        //判断订单是否存在
        if(orderInfo == null){
            return OrderStatus.NULL_ORDER.getStatus();
        }

        //返回订单状态
        return orderInfo.getStatus();
    }


    /**
     * 司机抢单
     * 基于Redisson解决并发问题
     * @param driverId
     * @param orderId
     * @return
     */
    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {

        //判断订单是否存在，使用redis，减少数据库压力
        if(!redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK)){
            //说明不存在该标识，抢单失败
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }

        //创建锁
        RLock lock = redissonClient.getLock("lock");

        try {
            //获取锁
            boolean flag = lock.tryLock(RedisConstant.ROB_NEW_ORDER_LOCK_WAIT_TIME, RedisConstant.ROB_NEW_ORDER_LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if(flag){
                if(!redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK)){
                    //说明不存在该标识，抢单失败
                    throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }
                //司机抢单
                //修改order_info表中的status状态值为 2(已接单) + 司机id + 司机接单时间
                //根据订单id修改
                LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OrderInfo::getId,orderId);
                OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
                orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
                orderInfo.setDriverId(driverId);
                orderInfo.setAcceptTime(new Date());
                //调用方法修改
                int rows = orderInfoMapper.updateById(orderInfo);
                if (rows != 1) {
                    throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }

                //删除抢单标识
                redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK);
            }

        } catch (GuiguException e) {
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch(Exception e){
            log.error("抢单异常 driverId={},orderId={}",e);
            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        } finally {
            //释放锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return true;
    }


    /**
     * 基于"乐观锁"解决司机抢单问题"并发"问题
     */
//    public Boolean robNewOrder1(Long driverId, Long orderId){
//        //判断订单是否存在，使用redis，减少数据库压力
//        if(!redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK)){
//            //说明不存在该标识，抢单失败
//            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
//        }
//
//        //司机抢单
//        //修改order_info表中的status状态值为 2(已接单) + 司机id + 司机接单时间
//        //update order_info set status = 2,driverId = ?,acceptTime = ?
//        // where id = ? and status = 1
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());
//        orderInfo.setDriverId(driverId);
//        orderInfo.setAcceptTime(new Date());
//
//        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(OrderInfo::getId,orderId);
//        wrapper.eq(OrderInfo::getStatus,OrderStatus.WAITING_ACCEPT.getStatus());
//
//        //调用方法修改
//        int rows = orderInfoMapper.update(orderInfo, wrapper);
//        if(rows != 1){
//            throw new GuiguException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
//        }
//
//        //删除抢单标识
//        redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK);
//
//        return true;
//    }
}
