package com.tanhua.server.controller;

import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 登录控制层(接收请求 响应结果)
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;
    /**
     * 登录-获取验证码
     *
     * ResponseEntity:响应实体对象
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody Map<String,String> params){
        //1.获取手机号
        String phone = params.get("phone");

        //2.调用业务层 发送短信验证码
        userService.sendCode(phone);
        return ResponseEntity.ok(null);
    }

    /**
     * 登录-校验（老用户直接登录 新用户自动注册-tb_user）
     */
    @RequestMapping(value = "/loginVerification",method = RequestMethod.POST)
    public ResponseEntity loginVerification(@RequestBody Map<String,String> params){
        //1.获取手机号
        String phone = params.get("phone");
        //2.获取验证码
        String code = params.get("verificationCode");
        //3.调用业务层 进行登录
        Map map = userService.login(phone,code); //{"token":"a.b.c","isNew":true}
        return ResponseEntity.ok(map);
    }


    /**
     * 首次登录---完善资料
     * ResponseEntity:响应实体对象
     */
    @RequestMapping(value = "/loginReginfo",method = RequestMethod.POST)
    public ResponseEntity loginReginfo(@RequestBody UserInfoVo userInfoVo){
        userInfoService.loginReginfo(userInfoVo);
        return ResponseEntity.ok(null);
    }



    /**
     * 首次登录---补充头像
     * ResponseEntity:响应实体对象
     *
     * springmvc接收文件对象
     * 方式一：MultipartFile headPhoto
     * 方式二：@RequestParam("headPhoto") MultipartFile xxxx
     */
    @RequestMapping(value = "/loginReginfo/head",method = RequestMethod.POST)
    public ResponseEntity loginReginfoHead(MultipartFile headPhoto){
        userInfoService.loginReginfoHead(headPhoto);
        return ResponseEntity.ok(null);
    }

}
