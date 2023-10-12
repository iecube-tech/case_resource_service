package com.iecube.community.model.auth.controller;

import com.iecube.community.basecontroller.auth.AuthBaseController;
import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.auth.service.IUserService;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController   // 注解相当于 @Controller + @RequestBody//表示此方法的响应结果以json格式进行数据的响应给到前端
@RequestMapping("/users")
public class UserControllerAuth extends AuthBaseController {
    @Autowired
    private IUserService userService;

    @PostMapping("/reg")
    public JsonResult<Void> reg(User user){
        //创建响应结果对象
        userService.reg(user);
        return new JsonResult<>(OK);
    }

    @PostMapping("/login")
    public JsonResult<User> login(String phoneNum, String password, HttpSession session){
        User user = userService.login(phoneNum,password);
        //向session对象中完成数据的绑定
        session.setAttribute("userid", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("type", "admin");
        // 获取session对象的数据
        log.info("login:{},{},{}",getUserTypeFromSession(session), getUserIdFromSession(session),getUsernameFromSession(session));
        return new JsonResult<User>(OK, user);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(HttpSession session){
        log.info("logout:{},{},{}",getUserTypeFromSession(session),getUserIdFromSession(session),getUsernameFromSession(session));
        session.invalidate();
        return new JsonResult<>(OK);
    }

    @PostMapping("/c_password")
    public JsonResult<Void> changePassword(String oldPassword, String newPassword, HttpSession session){
        Integer id = getUserIdFromSession(session);
        userService.changePassword(id, oldPassword, newPassword);

        return new JsonResult<Void>(OK);
    }

    @PostMapping("/c_phone")
    public JsonResult<Void> changePhoneNum(String newPhoneNum, String authCode, HttpSession session){
        Integer id = getUserIdFromSession(session);
        userService.changePhoneNum(id, newPhoneNum, authCode);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_email")
    public JsonResult<Void> changeEmail(String newEmail, String authCode, HttpSession session){
        Integer id  = getUserIdFromSession(session);
        userService.changeEmail(id, newEmail, authCode);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_user_group")
    public JsonResult<Void> changeUserGroup(Integer id, Integer userGroupId, HttpSession session ){
        Integer modifiedUserID = getUserIdFromSession(session);
        userService.changeUserGroup(id, userGroupId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_organization")
    public JsonResult<Void> changeOrganization(Integer id, Integer organizationId, HttpSession session){
        Integer modifiedUserID = getUserIdFromSession(session);
        userService.changeOrganization(id, organizationId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/c_direction")
    public JsonResult<Void> changeProductDirectionId(Integer id, Integer productDirectionId, HttpSession session){
        Integer modifiedUserID = getUserIdFromSession(session);
        userService.changeProductDirection(id, productDirectionId, modifiedUserID);

        return new JsonResult<>(OK);
    }

    @PostMapping("/delete")
    public JsonResult<Void> deleteUser(Integer id, HttpSession session){
        Integer modifiedUserID = getUserIdFromSession(session);
        userService.deleteUser(id, modifiedUserID);
        return new JsonResult<>(OK);
    }

}
