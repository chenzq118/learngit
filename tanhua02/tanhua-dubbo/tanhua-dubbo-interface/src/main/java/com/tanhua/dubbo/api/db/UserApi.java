package com.tanhua.dubbo.api.db;

import com.tanhua.model.db.User;

/**
 * 服务提供者接口
 */
public interface UserApi {
    /**
     * 根据手机号查询tb_user表 用户对象
     * @param phone
     * @return
     */
    User findByMobile(String phone);

    /**
     * 注册用户
     * @param user
     * @return
     */
    Long add(User user);
}
