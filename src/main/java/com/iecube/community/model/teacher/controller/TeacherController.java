package com.iecube.community.model.teacher.controller;
import com.iecube.community.basecontroller.auth.AuthBaseController;
import com.iecube.community.model.teacher.dto.LoginDto;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.qo.ChangePassword;
import com.iecube.community.model.teacher.service.TeacherService;
import com.iecube.community.model.teacher.vo.TeacherVo;
import com.iecube.community.model.usergroup.service.UserGroupService;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.jwt.AuthUtils;
import com.iecube.community.util.jwt.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teacher")
public class TeacherController extends AuthBaseController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public JsonResult<LoginDto> login(String email, String password){
        String agent = "Browser";
        LoginDto loginDto = teacherService.login(email,password);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserType("teacher");
        currentUser.setId(loginDto.getTeacher().getId());
        currentUser.setEmail(loginDto.getTeacher().getEmail());
        currentUser.setAgent(agent);
        AuthUtils.cache(currentUser, loginDto.getToken(), stringRedisTemplate);
        log.info("login:{},{},{}",currentUser.getUserType(),currentUser.getId(), currentUser.getEmail());
        List<String> teacherAuths = userGroupService.teacherAuth(loginDto.getTeacher().getId());
        loginDto.setAuthList(teacherAuths);
        return new JsonResult<>(OK, loginDto);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(){
        log.info("logout:{},{},{}",currentUserType(),currentUserId(),currentUserEmail());
        AuthUtils.rm(stringRedisTemplate);
        return new JsonResult<>(OK);
    }

    @PostMapping("/change_password")
    public JsonResult<Void> changePassword(@RequestBody ChangePassword changePassword){
        Integer teacherId= currentUserId();
        teacherService.changePassword(teacherId, changePassword.getOldPassword(), changePassword.getNewPassword());
        log.info("{} changePassword",teacherId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/add_teacher")
    public JsonResult<Void> addTeacher(@RequestBody Teacher newTeacher){
        Integer adminId = currentUserId();
        newTeacher.setCreator(adminId);
        newTeacher.setLastModifiedUser(adminId);
        newTeacher.setCreateTime(new Date());
        newTeacher.setLastModifiedTime(new Date());
        teacherService.insert(newTeacher);
        return new JsonResult<>(OK);
    }


    @GetMapping("/account")
    public JsonResult<Teacher> teacherAccount(){
        Integer teacherId = currentUserId();
        Teacher teacher = teacherService.teacherAccount(teacherId);
        return new JsonResult<>(OK, teacher);
    }

    @GetMapping("/teacher_list")
    public JsonResult<List> teacherList(){
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

    @GetMapping("/collage_teachers")
    public JsonResult<List> collageTeachers(){
        Integer teacher = currentUserId();
        List<Teacher> teacherList = teacherService.collageTeachers(teacher);
        return new JsonResult<>(OK, teacherList);
    }
}
