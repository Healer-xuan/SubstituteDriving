package com.atguigu.daijia.common.login;


import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 登录切面类
 */
@Component
@Aspect  //表示这个类是切面类
public class LoginAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    //环绕通知
    //切入点表达式，指定哪些规则的方法进行增强
    @Around("execution(* com.atguigu.daijia.*.controller.*.*(..)) && @annotation(xuanLogin)")
    public Object login(ProceedingJoinPoint proceedingJoinPoint, XuanLogin xuanLogin) throws Throwable {
        //1.获取request对象
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) attributes;
        HttpServletRequest request = sra.getRequest();

        //2.从请求头里获取token
        String token = request.getHeader("token");

        //3.判断token是否为空，如果为空则跳转到登录页面
        if(!StringUtils.hasText(token)){
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }

        //4.如果不为空，查询redis
        String customerId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        //5.查询redis对应用户id，把id放到LocalThread里面
        if (StringUtils.hasText(customerId)){
            AuthContextHolder.setUserId(Long.parseLong(customerId));
        }

        //6.执行业务方法
        return proceedingJoinPoint.proceed();
    }
}
