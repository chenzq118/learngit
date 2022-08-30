package com.tanhua.server.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.db.UserApi;
import com.tanhua.model.db.User;
import com.tanhua.model.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录业务处理层
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SmsTemplate smsTemplate;

    @DubboReference
    private UserApi userApi;


    /**
     * 登录-获取验证码
     *
     * @param phone
     */
    public void sendCode(String phone) {
        log.debug("*******手机号码{}******", phone);
        //1 根据key查询redis是否存在验证码
        String key = "VALIDATE_CODE_" + phone;
        //方式一：
        String redisCode = redisTemplate.opsForValue().get(key);
        //方式二：
        /*BoundValueOperations<String, String> bo = redisTemplate.boundValueOps(key);
        bo.get();
        bo.set(value);
        bo.get();
        bo.set(value);*/
        //2. 验证码存在，抛出异常 告知用户验证码未失效
        if (StringUtils.isNotEmpty(redisCode)) {
            //上一次发送的验证码还未失效
            throw new TanHuaException(ErrorResult.duplicate());
        }
        //3.验证码不存在，生成验证码6位
        //String randomNum = RandomStringUtils.randomNumeric(6);
        String randomNum = "111111";
        //4.调用阿里云短信接口发送短信
        if (false) {
            smsTemplate.sendSms(phone, randomNum);
        }
        //5.将验证码存入redis 5分钟有效
        redisTemplate.opsForValue().set(key, randomNum, 5, TimeUnit.MINUTES);
        log.debug("*******验证码发送成功了{}***验证码{}***", phone, randomNum);
    }

    /**
     * 登录-校验（老用户直接登录 新用户自动注册-tb_user）
     */
    public Map login(String phone, String code) {
        String key = "VALIDATE_CODE_" + phone;
        //1 根据key查询redis是否有验证码
        String redisCode = redisTemplate.opsForValue().get(key);
        //2 如果没有值，表示验证码已经过期
        if (StringUtils.isEmpty(redisCode)) {
            throw new TanHuaException(ErrorResult.loginError());
        }
        //3 如果有值，判断redis验证码跟用户输入验证码是否一致
        if (!redisCode.equals(code)) {
            //4 验证码如果不一致 抛出异常 验证码错误
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        //5 验证码正确 根据手机号查询tb_user表记录是否存在
        User user = userApi.findByMobile(phone);
        //6 如果不存在 自动注册用户
        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setMobile(phone);//手机号
            String password = phone.substring(5);
            // 密码加密
            password = SecureUtil.md5(password);
            user.setPassword(password);//预留字段 目前没有用
            Long userId = userApi.add(user); //返回用户主键id
            //将userId设置user对象中
            user.setId(userId);
            isNew = true;
        }
        //7 将验证码从redis删除
        redisTemplate.delete(key);
        //8 生成token 存入redis 1天有效(redis可以续期)
        String token = JwtUtils.createToken(user.getId(), phone);
        //8.1:key=固定前缀_+token（a.b.c）   value=user对象转为字符串存入
        String tokenKey = "TOKEN_" + token;
        String userStr = JSON.toJSONString(user); //将对象 转为 字符串 
        //User uuu = JSON.parseObject(userStr, User.class);//将字符串 转为 对象
        //一举两得 1:防止伪造token 2:后续业务中会使用到用户信息
        redisTemplate.opsForValue().set(tokenKey, userStr, 1, TimeUnit.DAYS);
        //9 构造Map 返回{"token":"a.b.c","isNew":true}
        Map map = new HashMap();
        map.put("token", token);
        map.put("isNew", isNew);
        return map;
    }


}
