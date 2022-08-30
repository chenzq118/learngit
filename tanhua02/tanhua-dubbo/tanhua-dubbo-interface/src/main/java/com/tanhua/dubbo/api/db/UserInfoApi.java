package com.tanhua.dubbo.api.db;

import com.tanhua.model.db.UserInfo;

/**
 * 用户服务接口
 */
public interface UserInfoApi {
    /**
     * 保存用户信息
     * @param userInfo
     */
    void add(UserInfo userInfo);

    /**
     *更新tb_user_info表 数据
     * @param userInfo
     */
    void update(UserInfo userInfo);

    /**
     * 根据用户id查询用户信息
     * @param currentUserId
     * @return
     */
    UserInfo findUserInfo(Long currentUserId);
}
