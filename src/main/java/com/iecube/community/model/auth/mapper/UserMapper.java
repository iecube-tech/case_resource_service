package com.iecube.community.model.auth.mapper;

import com.iecube.community.model.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**用户模块的持久层接口**/
@Mapper
public interface UserMapper {
    /**
     * 插入用户数据
     * @param user 用户的数据
     * @return 受影响的行数（增，删，改，都受影响的行数作为返回值，可以根据返回值来判断是否执行成功）
     */
    Integer insert(User user);

    /**
     * 根据用户名查询用户数据
     * @param username 用户名
     * @return 如果找到对应的用户 则返回用户数据  如果没有找到 则返回null
     */
    User findByUsername(String username);

    /**
     * 根据手机号查询用户数据
     * @param PhoneNum 用户名
     * @return 如果找到对应的用户 则返回用户数据  如果没有找到 则返回null
     */
    User findByPhoneNum(String PhoneNum);

    /**
     * 根据用户邮箱查询用户数据
     * @param email 用户email
     * @return 如果找到对应用户，则返回user对象， 如果没有找到，则返回null
     */
    User findByEmail(String email);

    /**
     * 根据用户id修改用户密码
     * @param id 用户的id
     * @param password 修改的密码 用户输入新密码
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updatePasswordByUserId(Integer id, String password, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 根据用户id查询用户数据
     * @param id 用户id
     * @return 如果找到返回user对象  如果没有返回null
     */
    User findByUserId(Integer id);

    /**
     * 更新用户手机号
     * @param id 用户id
     * @param phoneNum 用户新手机号
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updatePhoneNum(Integer id, String phoneNum, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 更新用户Email
     * @param id 用户id
     * @param email 用户Email
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updateEmail(Integer id, String email, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 更新用户的用户组
     * @param id 用户id
     * @param userGroup 用户组id
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updateUserGroup(Integer id, Integer userGroup, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 更新用户组织/学校
     * @param id 用户id
     * @param organizationId 组织id
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updateOrganization(Integer id, Integer organizationId, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 更新用户的关注的产品方向
     * @param id 用户id
     * @param productDirectionId 产品方向id
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响的行数
     */
    Integer updateProductDirectionId(Integer id, Integer productDirectionId, Integer lastModifiedUser, Date lastModifiedTime);

    /**
     * 删除用户
     * @param id 用户id
     * @param lastModifiedUser 最后修改人
     * @param lastModifiedTime 最后修改时间
     * @return 受影响行数
     */
    Integer deleteUser(Integer id, Integer lastModifiedUser, Date lastModifiedTime);


    List<User> findByGroup(Integer userGroup);

    Integer findTypeIdById(Integer id);
}

