package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.FaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.dubbo.api.db.UserInfoApi;
import com.tanhua.model.db.User;
import com.tanhua.model.db.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 完善用户信息业务处理层
 */
@Service
@Slf4j
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private FaceTemplate faceTemplate;

    @Autowired
    private OssTemplate ossTemplate;


    /**
     * 首次登录---完善资料
     * ResponseEntity:响应实体对象
     */
    public void loginReginfo(UserInfoVo userInfoVo) {
        //3.如果已经登录，保存用户信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);
        userInfo.setId(UserHolder.getUserId());//设置tb_userInfo表中主键id
        // 设置年龄
        userInfo.setAge(getAge(userInfoVo.getBirthday()));
        userInfoApi.add(userInfo);
    }

    /**
     * 根据token获取用户对象
     * @param token
     * @return
     */
    /*private User getUserByToken(String token) {
        String tokenKey ="TOKEN_" + token;
        //1 根据key查询redis用户是否登录
        String userStr = stringRedisTemplate.opsForValue().get(tokenKey);
        //2 如果没有登录 抛出异常
        if(StringUtils.isEmpty(userStr)){
            throw new TanHuaException(ErrorResult.loginFail());
        }
        //3.续期
        stringRedisTemplate.expire(tokenKey,1, TimeUnit.DAYS);
        return JSON.parseObject(userStr, User.class);
    }*/


    /**
     * 通过年-月-日获取年龄
     * @param yearMonthDay
     * @return
     */
    private int getAge(String yearMonthDay){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDay = sdf.parse(yearMonthDay);
            Years years = Years.yearsBetween(new DateTime(birthDay), DateTime.now());
            return years.getYears();
        } catch (ParseException e) {
            log.error("获取用户年龄失败!", e);
            return 0;
        }
    }

    /**
     * 首次登录---补充头像
     * @param headPhoto
     */
    public void loginReginfoHead(MultipartFile headPhoto) {
        try {
            //1, 判断用户是否登录
            //2. 调用百度云人脸识别
            boolean flag = faceTemplate.detect(headPhoto.getBytes());
            //3. 识别失败 则抛出异常
            if(!flag){
                throw new TanHuaException(ErrorResult.faceError());
            }
            //4. 识别成功 调用阿里云oss保存头像
            String filename = headPhoto.getOriginalFilename() ;//获取原始文件名称
            String avatar = ossTemplate.upload(filename, headPhoto.getInputStream());
            //5. 根据用户id修改tb_user_info表中的头像
            UserInfo userInfo = new UserInfo();
            userInfo.setId(UserHolder.getUserId());
            userInfo.setAvatar(avatar);
            userInfoApi.update(userInfo);
        } catch (IOException e) {
            throw new TanHuaException(ErrorResult.error());
        }
    }

    /**
     * 用户资料 - 读取
     *
     */
    public UserInfoVo findUserInfo(Long userID) {
        //1, 判断用户是否登录
        Long currentUserId = UserHolder.getUserId();
        //2 根据用户id查询userInfo
        if(userID != null){
            currentUserId = userID;
        }
        UserInfo userInfo  = userInfoApi.findUserInfo(currentUserId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,userInfoVo);
        userInfoVo.setAge(userInfo.getAge()+"");
        return userInfoVo;
    }

    /**
     * 用户资料 - 保存(更新)
     *
     */
    public void updateUserInfo(UserInfoVo userInfoVo) {
        //1, 判断用户是否登录
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);
        userInfo.setId(UserHolder.getUserId());//当前用户id
        //3. 计算年龄
        userInfo.setAge(getAge(userInfoVo.getBirthday()));
        //2 更新用户信息
        userInfoApi.update(userInfo);
    }
}
