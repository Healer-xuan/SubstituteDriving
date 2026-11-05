package com.atguigu.daijia.map.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.constant.SystemConstant;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.LocationUtil;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.map.repository.OrderServiceLocationRepository;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;
import com.atguigu.daijia.model.vo.map.OrderLocationVo;
import com.atguigu.daijia.model.vo.map.OrderServiceLastLocationVo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private OrderServiceLocationRepository orderServiceLocationRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 更新司机位置信息
     * @param updateDriverLocationForm
     * @return
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(),
                                updateDriverLocationForm.getLatitude().doubleValue());
        //添加到Redis里面
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION,point,updateDriverLocationForm.getDriverId().toString());
        return true;
    }

    /**
     * 删除司机位置信息
     * @param driverId
     * @return
     */
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION,driverId.toString());
        return true;
    }

    /**
     * 搜素附近满足条件的司机信息
     * @param searchNearByDriverForm
     * @return
     */
    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {
        //搜素经纬度位置5公里以内的司机

        //1.操作redis里的GEO
        //创建circle对象，point  distance
        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(),
                                searchNearByDriverForm.getLatitude().doubleValue());

        Distance distance = new Distance(SystemConstant.ACCEPT_DISTANCE,
                                         RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(point,distance);

        //定义GEO参数，设置返回结果包含内容
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()  //包含距离
                .includeCoordinates()  //包含坐标
                .sortAscending();  //升序


        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);

        //2.查询redis最终返回list集合
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = results.getContent();

        //3 对查询list集合进行处理
        // 遍历list集合，得到每个司机信息
        // 根据每个司机个性化设置信息判断
        List<NearByDriverVo> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(content)){
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();
            while(iterator.hasNext()){
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

                //获取司机id
                Long driverId = Long.parseLong(item.getContent().getName());

                //远程调用，根据司机id个性化设置信息
                Result<DriverSet> driverSetResult = driverInfoFeignClient.getDriverSet(driverId);
                DriverSet driverSet = driverSetResult.getData();

                //判断订单里程order_distance
                BigDecimal orderDistance = driverSet.getOrderDistance();
                //订单里程距离 — 当前单子距离 < 0 则不符合条件
                if(orderDistance.doubleValue() != 0 &&
                        orderDistance.subtract(searchNearByDriverForm.getMileageDistance()).doubleValue() < 0){
                    continue;
                }

                //判断接单里程(距离乘客的距离)
                BigDecimal acceptDistance = driverSet.getAcceptDistance();
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, RoundingMode.HALF_UP);
                if(acceptDistance.doubleValue() != 0 &&
                acceptDistance.subtract(currentDistance).doubleValue() < 0){
                    continue;
                }

                //封装符合条件的数据
                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);

            }
        }

        return list;
    }


    /**
     * 更新订单司机位置信息到Redis
     * @param updateOrderLocationForm
     * @return
     */
    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        String key = RedisConstant.UPDATE_ORDER_LOCATION + updateOrderLocationForm.getOrderId();

        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLongitude(updateOrderLocationForm.getLongitude());
        orderLocationVo.setLatitude(updateOrderLocationForm.getLatitude());

        redisTemplate.opsForValue().set(key,orderLocationVo);

        return true;
    }


    /**
     * 乘客端获取司机经纬度位置
     * @param orderId
     * @return
     */
    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        //调用Redis查看
        String key = RedisConstant.UPDATE_ORDER_LOCATION + orderId;
        OrderLocationVo orderLocationVo = (OrderLocationVo)redisTemplate.opsForValue().get(key);
        return orderLocationVo;
    }



    /**
     * 批量保存代驾服务订单位置
     * @param orderLocationServiceFormList
     * @return
     */
    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList) {
        orderLocationServiceFormList.forEach(orderServiceLocationForm -> {
            OrderServiceLocation orderServiceLocation = new OrderServiceLocation();
            BeanUtils.copyProperties(orderServiceLocationForm,orderServiceLocation);
            orderServiceLocation.setId(ObjectId.get().toString());
            orderServiceLocation.setCreateTime(new Date());

            orderServiceLocationRepository.save(orderServiceLocation);
        });

        return true;
    }


    /**
     * 获取代驾服务订单最后一个位置信息
     * @param orderId
     * @return
     */
    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {
        //查询MongoDB
        //查询条件 ：orderId
        //根据创建时间降序排列
        //最新一条数据
        Query query = new Query();
        query.addCriteria(Criteria.where("orderId").is(orderId));
        query.with(Sort.by(Sort.Order.desc("createTime")));
        query.limit(1);

        OrderServiceLocation orderServiceLocation = mongoTemplate.findOne(query, OrderServiceLocation.class);

        //封装到OrderServiceLastLocationVo
        OrderServiceLastLocationVo orderServiceLastLocationVo = new OrderServiceLastLocationVo();
        BeanUtils.copyProperties(orderServiceLocation,orderServiceLastLocationVo);

        return orderServiceLastLocationVo;
    }


    /**
     * 代驾服务：计算订单实际里程
     * @param orderId
     * @return
     */
    @Override
    public BigDecimal calculateOrderRealDistance(Long orderId) {
        //1 根据订单id获取代驾订单位置信息，根据创建时间排序(升序)
        //查询Mongodb

        //第一种方式：
        OrderServiceLocation orderServiceLocation = new OrderServiceLocation();
        orderServiceLocation.setOrderId(orderId);
        Example<OrderServiceLocation> example = Example.of(orderServiceLocation);
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        List<OrderServiceLocation> list = orderServiceLocationRepository.findAll(example, sort);

        //第二种方式： MongoRepository只需要 按照规则 在MongoRepository中把查询方法创建出来就可以了
        //总体规则：
        //1 查询方法名称 以 get  |  find  | read开头
        //2 后面查询字段名称，满足驼峰式命名，比如OrderId
        //3 字段查询条件添加关键字，比如Like  OrderBy   Asc
        // 具体编写 ： 根据订单id获取代驾订单位置信息，根据创建时间排序（升序）
//        List<OrderServiceLocation> list =
//                orderServiceLocationRepository.findByOrderIdOrderByCreateTimeAsc(orderId);


        //2 遍历list集合，得到每个位置信息，计算两个位置距离
        //把计算所有距离相加操作
        double realDistance = 0;
        if(!CollectionUtils.isEmpty(list)){
            for (int i = 0, size = list.size() - 1; i <  size; i++) {
                OrderServiceLocation location1 = list.get(i);
                OrderServiceLocation location2 = list.get(i + 1);

                //调用工具类计算位置距离
                double distance = LocationUtil.getDistance(location1.getLatitude().doubleValue(),
                                                           location1.getLongitude().doubleValue(),
                                                           location2.getLatitude().doubleValue(),
                                                           location2.getLongitude().doubleValue());
                realDistance += distance;
            }
        }

        //返回最终计算实际距离
        //注意：LocationUtil.getDistance 返回的是【米】，而费用规则(FeeRule.drl)里的距离单位是【公里】
        //这里必须除以1000，否则里程会被放大1000倍，费用计算全错
        return BigDecimal.valueOf(realDistance / 1000).setScale(2, RoundingMode.HALF_UP);
    }
}
