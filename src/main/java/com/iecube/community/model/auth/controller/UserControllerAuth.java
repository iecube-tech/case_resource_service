package com.iecube.community.model.auth.controller;

import com.iecube.community.basecontroller.auth.AuthBaseController;
import com.iecube.community.model.auth.dto.LoginDto;
import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.auth.service.IUserService;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.jwt.AuthUtils;
import com.iecube.community.util.jwt.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController   // 注解相当于 @Controller + @RequestBody//表示此方法的响应结果以json格式进行数据的响应给到前端
@RequestMapping("/users")
public class UserControllerAuth extends AuthBaseController {
    @Autowired
    private IUserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/reg")
    public JsonResult<Void> reg(User user){
        //创建响应结果对象
        userService.reg(user);
        return new JsonResult<>(OK);
    }

    @PostMapping("/login")
    public JsonResult<LoginDto> login(String phoneNum, String password){
        LoginDto loginDto = userService.login(phoneNum,password);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserType("admin");
        currentUser.setId(loginDto.getUser().getId());
        currentUser.setEmail(loginDto.getUser().getPhoneNum());
        AuthUtils.cache(currentUser, loginDto.getToken(), stringRedisTemplate);
        log.info("login:{},{},{}",currentUser.getUserType(),currentUser.getId(), currentUser.getEmail());
        return new JsonResult<>(OK, loginDto);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(){
        AuthUtils.rm(stringRedisTemplate);
        log.info("logout:{},{},{}",currentUserType(),currentUserId(),currentUserEmail());
        return new JsonResult<>(OK);
    }

    @PostMapping("/c_password")
    public JsonResult<Void> changePassword(String oldPassword, String newPassword){
        Integer id = currentUserId();
        userService.changePassword(id, oldPassword, newPassword);

        return new JsonResult<Void>(OK);
    }

    @PostMapping("/c_phone")
    public JsonResult<Void> changePhoneNum(String newPhoneNum, String authCode){
        Integer id = currentUserId();
        userService.changePhoneNum(id, newPhoneNum, authCode);
        return new JsonResult<>(OK);
    }

    @PostMapping("/c_email")
    public JsonResult<Void> changeEmail(String newEmail, String authCode){
        Integer id  = currentUserId();
        userService.changeEmail(id, newEmail, authCode);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_user_group")
    public JsonResult<Void> changeUserGroup(Integer id, Integer userGroupId ){
        Integer modifiedUserID = currentUserId();
        userService.changeUserGroup(id, userGroupId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_organization")
    public JsonResult<Void> changeOrganization(Integer id, Integer organizationId){
        Integer modifiedUserID = currentUserId();
        userService.changeOrganization(id, organizationId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_direction")
    public JsonResult<Void> changeProductDirectionId(Integer id, Integer productDirectionId){
        Integer modifiedUserID = currentUserId();
        userService.changeProductDirection(id, productDirectionId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/delete")
    public JsonResult<Void> deleteUser(Integer id){
        Integer modifiedUserID = currentUserId();
        userService.deleteUser(id, modifiedUserID);
        return new JsonResult<>(OK);
    }

}
