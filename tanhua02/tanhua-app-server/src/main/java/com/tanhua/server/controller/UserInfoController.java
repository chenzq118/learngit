package com.tanhua.server.controller;

import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层(接收请求 响应结果)
 */
@RestController
@RequestMapping("/users")
public class UserInfoController {


    @Autowired
    private UserInfoService userInfoService;
    /**
     * 用户资料 - 读取
     *
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findUserInfo(Long userID){
        UserInfoVo userInfoVo = userInfoService.findUserInfo(userID);
        return ResponseEntity.ok(userInfoVo);
    }


    /**
     * 用户资料 - 保存(更新)
     *
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUserInfo(@RequestBody UserInfoVo userInfoVo){
        userInfoService.updateUserInfo(userInfoVo);
        return ResponseEntity.ok(null);
    }

}
