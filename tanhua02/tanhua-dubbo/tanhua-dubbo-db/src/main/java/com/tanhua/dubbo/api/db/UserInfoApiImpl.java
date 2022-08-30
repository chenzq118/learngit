package com.tanhua.dubbo.api.db;

import com.tanhua.dubbo.api.mapper.UserInfoMapper;
import com.tanhua.model.db.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户信息服务接口实现类
 */
@DubboService
public class UserInfoApiImpl implements UserInfoApi{

    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * 保存用户信息
     * @param userInfo
     */
    @Override
    public void add(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    /**
     *更新tb_user_info表 数据
     * @param userInfo
     */
    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据用户id查询用户信息
     * @param currentUserId
     * @return
     */
    @Override
    public UserInfo findUserInfo(Long currentUserId) {
        return userInfoMapper.selectById(currentUserId);
    }
}
