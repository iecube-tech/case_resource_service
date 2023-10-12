package com.iecube.community.model.auth.service;

import com.iecube.community.model.auth.entity.User;

/**用户模块业务层接口**/
public interface IUserService {
    /**
     * 用户注册方法
     * @param user 用户数据对象
     */
    void reg(User user);

    /**
     * 用户登录功能
     * @param phoneNum 用户名
     * @param password 密码
     * @return 当前匹配的用户数据  如果没有则返回null
     */
    User login(String phoneNum, String password);

    /**
     * 用户修改密码
     * @param id 用户id
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(Integer id, String oldPassword, String newPassword);

    void changePhoneNum(Integer id, String newPhoneNum, String authCode);

    void changeEmail(Integer id, String email, String authCode);

    void changeUserGroup(Integer id, Integer userGroupId, Integer lastModifiedUserId);

    void changeOrganization(Integer id, Integer organizationId, Integer lastModifiedUserId);

    void changeProductDirection(Integer id, Integer ProductDirectionId, Integer lastModifiedUserId);

    void deleteUser(Integer id, Integer lastModifiedUserId);
}
