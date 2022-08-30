package com.tanhua.dubbo.api.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.api.mapper.UserMapper;
import com.tanhua.model.db.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户服务接口实现类
 */
@DubboService
public class UserApiImpl implements UserApi{

    @Autowired
    private UserMapper userMapper;


    /**
     * 根据手机号查询tb_user表 用户对象
     * @param phone
     * @return
     */
    @Override
    public User findByMobile(String phone) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("mobile",phone);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public Long add(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
