package com.iecube.community.model.auth.service.impl;

import com.iecube.community.model.auth.dto.LoginDto;
import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.auth.mapper.UserMapper;
import com.iecube.community.model.auth.service.IUserService;
import com.iecube.community.model.auth.service.ex.*;
import com.iecube.community.util.jwt.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * 用户模块业务层实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 实际上要做的是  调用mapper层的方法把user对象传递下去
     * 用户注册实现方法
     * @param user 用户数据对象
     */
    @Override
    public void reg(User user) {
        // 通过user参数来获取传递过来的PhoneNum
        // 调用findByPhoneNum(PhoneNum) 判断用户是否被注册
        User result = userMapper.findByPhoneNum(user.getPhoneNum());
        //  判断结果集是否为null 不为null 则抛出用户已存在的异常
        if (result != null){
            //抛出异常
            throw new PhoneNumDuplicatedException("手机号已存在。");
        }
        // 补全注册数据 is_delete 4个日志(谁操作)
        user.setIsDelete(0);
        user.setVipTypeId(0);
        Date date = new Date();
        user.setCreateTime(date);
        user.setLastModifiedTime(date);
        /**需要获取当前登录人的信息完善操作人信息**/
        user.setCreator(0);
        user.setLastModifiedUser(0);
        user.setTypeId(0);
        user.setOrganizationId(0);
        user.setProductDirectionId(0);
        user.setUserGroup(0);
        user.setGender(1);
        // 密码加密处理 md5 加密算法
        // （串 + password + 串）  全部交给md5加密 连续加载3次
        // 盐值 + password + 盐值 盐值就是一个随机的字符串
        String oldPassword = user.getPassword();
        // 获取盐值（随机生成一个盐值）
        String salt = UUID.randomUUID().toString().toUpperCase();
        //将密码和盐值作为一个整体进行加密处理
        String md5Password = getMD5Password(oldPassword, salt);
        // 将加密后的密码重新补全到user中去
        user.setPassword(md5Password);
        user.setSalt(salt);


        // 执行注册业务逻辑
        Integer rows = userMapper.insert(user);
        if (rows != 1){
            throw new InsertException("在用户注册过程中产生未知异常");
        }

    }

    @Override
    public LoginDto login(String phoneNum, String password) {
        // 根据用户名称来查询用户的数据是否存在 如果不存在则抛异常
        User result = userMapper.findByPhoneNum(phoneNum);
        if (result == null){
            throw new UserNotFoundException("用户数据不存在");
        }
        // 检测密码
        // 先获取数据库加密后的密码 盐值  和用户传递过来的密码(相同的方法进行加密)进行比较
        String salt = result.getSalt();
        String oldMd5Password = result.getPassword();
        String newMd5Password = getMD5Password(password, salt);
        if (!newMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("用户密码错误");
        }

        // 判断 is_delete字段的值是否为1，表示已被删除
        if(result.getIsDelete() == 1){
            throw new UserNotFoundException("用户数据不存在");
        }
        result.setPassword(null);
        result.setSalt(null);
        LoginDto loginDto = new LoginDto();
        loginDto.setUser(result);
        loginDto.setToken(new AuthUtils().createToken(result.getId(), result.getPhoneNum(), "admin"));
        return loginDto;
    }

    /**定义一个md5算法加密**/
    private String getMD5Password(String password, String salt){
        // md5加密算法的方法 进行3次
        for (int i=0; i<3; i++){
            password = DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
    }

    @Override
    public void changePassword(Integer id, String oldPassword, String newPassword) {
       User result =  userMapper.findByUserId(id);
       if (result == null || result.getIsDelete()==1){
           throw new UserNotFoundException("用户数据不存在");
       }
       //原始密码和数据库中密码比较
        String oldMd5Password = getMD5Password(oldPassword, result.getSalt());
       if(!result.getPassword().equals(oldMd5Password)){
           throw new PasswordNotMatchException("原密码错误");
       }
       // 将新密码加密写入
        String newMd5Password = getMD5Password(newPassword, result.getSalt());
       Integer rows = userMapper.updatePasswordByUserId(id, newMd5Password, id, new Date());
       if (rows != 1){
           throw new UpdateException("更新数据发生异常");
       }
    }

    @Override
    public void changePhoneNum(Integer id, String newPhoneNum, String authCode) {
        User result = userMapper.findByUserId(id);
        if (result == null || result.getIsDelete() == 1){
            throw new UserNotFoundException("用户数据不存在");
        }
        User result2 = userMapper.findByPhoneNum(newPhoneNum);
        if (result2 != null){
            throw new PhoneNumDuplicatedException("新手机号已存在");
        }
        /**保留的验证码完善**/
        if (authCode == ""){
            throw new AuthCodeErrorException("验证码错误");
        }
        Integer rows = userMapper.updatePhoneNum(id,newPhoneNum,id,new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }
    }

    @Override
    public void changeEmail(Integer id, String email, String authCode) {
        User result = userMapper.findByUserId(id);
        if(result == null){
            throw new UserNotFoundException("用户未找到");
        }
        User result2 = userMapper.findByEmail(email);
        if(result2 != null){
            throw new EmailDuplicateException("新邮箱已存在");
        }
        /**保留的邮箱验证码完善**/
        if (authCode == ""){
            throw new AuthCodeErrorException("验证码错误");
        }
        Integer rows = userMapper.updateEmail(id, email, id, new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }
    }

    @Override
    public void changeUserGroup(Integer id, Integer userGroupId, Integer lastModifiedUserId) {
        User result = userMapper.findByUserId(id);
        if (result == null){
            throw new UserNotFoundException("用户未找到");
        }
        /**添加用户组是否存在的判断 不存在抛出异常UserGroupNotFoundException**/

        /**判断用户类型和用户组类型，类型不同不能添加到组** 抛出异常CanNotAddToUserGroupException**/

        /**判断用户的产品方向和用户组产品方向是否一致，不一致修改为一致**/

        Integer rows = userMapper.updateUserGroup(id, userGroupId, lastModifiedUserId, new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }

    }

    @Override
    public void changeOrganization(Integer id, Integer organizationId, Integer lastModifiedUserId) {
        User result = userMapper.findByUserId(id);
        if (result == null){
            throw new UserNotFoundException("用户未找到");
        }
        /**添加 组织/学校 是否存在的判断， 不存在破除异常 OrganizationNotFoundException**/
        /**判断 用户类型和组织类型， 不相同则不能添加， 抛出异常 CanNotAddToOrganizationException **/

        Integer rows = userMapper.updateOrganization(id, organizationId, lastModifiedUserId, new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }
    }

    @Override
    public void changeProductDirection(Integer id, Integer ProductDirectionId, Integer lastModifiedUserId) {
        User result = userMapper.findByUserId(id);
        if (result == null){
            throw new UserNotFoundException("用户未找到");
        }
        /** 判断ProductDirectionId是否存在 不存在抛出 NotFoundProductDirection 异常 **/

        Integer rows = userMapper.updateProductDirectionId(id, ProductDirectionId, lastModifiedUserId, new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }
    }

    @Override
    public void deleteUser(Integer id, Integer lastModifiedUserId) {
        User result = userMapper.findByUserId(id);
        if (result == null){
            throw new UserNotFoundException("用户未找到");
        }
        /** 根据 lastModifiedUserId 判断是不是管理员 不是管理员抛出 PermissionDeniedException **/

        Integer rows = userMapper.deleteUser(id, lastModifiedUserId, new Date());
        if (rows != 1){
            throw new UpdateException("更新数据发生异常");
        }
    }


}
