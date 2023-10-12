package com.iecube.community.model.teacher.controller;
import com.iecube.community.basecontroller.auth.AuthBaseController;
import com.iecube.community.model.teacher.entity.Tags;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.qo.ChangePassword;
import com.iecube.community.model.teacher.service.TeacherService;
import com.iecube.community.model.teacher.vo.TeacherVo;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/teacher")
public class TeacherController extends AuthBaseController {
    @Autowired
    private TeacherService teacherService;

    @PostMapping("/login")
    public JsonResult<Teacher> login(HttpSession session, String email, String password){
        Teacher user = teacherService.login(email,password);
        //向session对象中完成数据的绑定
        session.setAttribute("userid", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("type", "teacher");
        // 获取session对象的数据
        log.info("login:{},{},{}", getUserTypeFromSession(session),getUserIdFromSession(session),getUsernameFromSession(session));
        return new JsonResult<>(OK, user);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(HttpSession session){
        log.info("logout:{},{},{}",getUserTypeFromSession(session),getUserIdFromSession(session),getUsernameFromSession(session));
        session.invalidate();
        return new JsonResult<>(OK);
    }

    @PostMapping("change_password")
    public JsonResult<Void> changePassword(@RequestBody ChangePassword changePassword, HttpSession session){
        Integer teacherId= getUserIdFromSession(session);
        teacherService.changePassword(teacherId, changePassword.getOldPassword(), changePassword.getNewPassword());
        log.info("{} changePassword",teacherId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/account")
    public JsonResult<Teacher> teacherAccount(HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        Teacher teacher = teacherService.teacherAccount(teacherId);
        return new JsonResult<>(OK, teacher);
    }

    @GetMapping("/teacher_list")
    public JsonResult<List> teacherList(HttpSession session){
        List<TeacherVo> teacherVoList = teacherService.getTeacherVoList();
        return new JsonResult<>(OK,teacherVoList);
    }

    @GetMapping("/enable")
    public  JsonResult<Void> teacherEnable(Integer teacherId){
        teacherService.teacherEnable(teacherId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/disable")
    public  JsonResult<Void> teacherDisable(Integer teacherId){
        teacherService.teacherDisable(teacherId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/teacher_info")
    public JsonResult<TeacherVo> teacherInfo(Integer teacherId){
        TeacherVo teacherVo = teacherService.teacherInfo(teacherId);
        return new JsonResult<>(OK, teacherVo);
    }
}
