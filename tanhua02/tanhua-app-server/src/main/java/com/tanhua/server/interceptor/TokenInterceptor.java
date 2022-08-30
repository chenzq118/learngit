package com.tanhua.server.interceptor;

import com.alibaba.fastjson.JSON;
import com.tanhua.model.db.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 统一处理token
 */
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 前置处理 进入controller之前处理
     * 1.登录请求 要放行(配置类配置即可)
     * 2.非登录请求 判断用户是否登录
     * 2.1 如果用户没有登录 直接返回401（用户没有访问权限，需要进行身份认证）
     * 2.2 如果用户登录了，放行 并将用户信息存入ThreadLocal
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.打印请求路径信息
        String requestMethod = request.getMethod();
        log.info("请求了路径: {}:{}", requestMethod, request.getRequestURI());
        if (handler instanceof HandlerMethod) {
            // 找出调用的哪个controller中的方法
            HandlerMethod method = (HandlerMethod) handler;
            if (null != method) {
                log.info("执行了: " + method.getShortLogMessage());
            }
        }
        //2.获取请求头中token信息
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token)) {
            //3.根据请求头中token查询redis中用户信息userStr
            String tokenKey ="TOKEN_" + token;
            String userStr = stringRedisTemplate.opsForValue().get(tokenKey);
            //4.根据userStr转user对象 存入ThreadLocal
            if(StringUtils.isNotEmpty(userStr)){
                User user = JSON.parseObject(userStr, User.class);
                UserHolder.setUser(user);
                stringRedisTemplate.expire(tokenKey,1, TimeUnit.DAYS);
                //4.1 放行
                return true;
            }
        }
        //5.不满足以上要求 返回权限不足信息
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
