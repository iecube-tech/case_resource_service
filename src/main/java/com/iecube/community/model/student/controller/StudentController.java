package com.iecube.community.model.student.controller;

import com.iecube.community.basecontroller.student.StudentBaseController;
import com.iecube.community.model.auth.dto.LoginDto;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.model.student.qo.DeleteQo;
import com.iecube.community.model.student.service.StudentService;
import com.iecube.community.model.teacher.qo.ChangePassword;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.ex.SystemException;
import com.iecube.community.util.jwt.AuthUtils;
import com.iecube.community.util.jwt.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController extends StudentBaseController {

    private static final String TEMPLATE_NAME = "学生导入模板.xls";

    private final List<String> clientAgents= Arrays.asList("iecube3835");
    @Value("${business.user.template.path}")
    private Resource userImportTemplate;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/list")
    public JsonResult<List> findStudentsLimitByTeacherId(Integer page, Integer pageSize){
        Integer teacherId = currentUserId();
        List<StudentDto> students = studentService.findStudentsLimitByTeacherId(teacherId, page, pageSize);
        return new JsonResult<>(OK, students);
    }

    @GetMapping("/num")
    public JsonResult<Integer> StudentNum(){
        Integer teacherId = currentUserId();
        Integer studentsNum = studentService.studentsNum(teacherId);
        return new JsonResult<>(OK, studentsNum);
    }

    @GetMapping("/all")
    public JsonResult<List> findAllInStatusByTeacher(){
        Integer teacherId = currentUserId();
        List<StudentDto> students = studentService.findAllInStatusByTeacher(teacherId);
        return new JsonResult<>(OK,students);
    }

    @PostMapping("/login")
    public  JsonResult<LoginDto> login(String email, String password, @RequestHeader("User-Agent") String userAgent, HttpSession session){

        String agent="Browser"; // 区分浏览器还是 设备， 允许一个账号登录在 一个浏览器 一个软件端
        LoginDto loginDto = studentService.jwtLogin(email, password);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserType("student");
        currentUser.setId(loginDto.getStudentDto().getId());
        currentUser.setEmail(loginDto.getStudentDto().getEmail());
        currentUser.setAgent(agent);
        session.setAttribute("userid", loginDto.getStudentDto().getId());
        session.setAttribute("username", loginDto.getStudentDto().getStudentName());
        session.setAttribute("type", "student");
        AuthUtils.cache(currentUser, loginDto.getToken(), stringRedisTemplate);
        log.info("login:{},{},{},{}",currentUser.getUserType(),currentUser.getId(), currentUser.getEmail(), currentUser.getAgent());
        if(clientAgents.contains(userAgent)){
            // 设备登录
            CurrentUser deviceUser = new CurrentUser();
            deviceUser.setAgent(userAgent);
            deviceUser.setUserType("student");
            deviceUser.setId(loginDto.getStudentDto().getId());
            deviceUser.setEmail(loginDto.getStudentDto().getEmail());
            AuthUtils.cache(deviceUser, loginDto.getToken(), stringRedisTemplate);
            log.info("login:{},{},{},{}",deviceUser.getUserType(),deviceUser.getId(), deviceUser.getEmail(),deviceUser.getAgent());
        }
        return new JsonResult<>(OK,loginDto);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(){
        AuthUtils.rm(stringRedisTemplate);
        log.info("logout:{},{},{}",currentUserType(),currentUserId(),currentUserEmail());
        return new JsonResult<>(OK);
    }

    @PostMapping("/add")
    public JsonResult<Void> addStudent(@RequestBody AddStudentQo addStudentQo){
        Integer teacherId = currentUserId();
        studentService.addStudent(addStudentQo, teacherId);
        return new JsonResult<>(OK);
    }

    @GetMapping(value = "/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            DownloadUtil.httpDownload(this.userImportTemplate.getInputStream(), TEMPLATE_NAME, response);
        } catch (IOException e) {
            log.error("IO异常", e);
            throw new SystemException("下载异常");
        }
    }

    @PostMapping(value = "/batch/excel")
    public JsonResult<Void> importByExcel(MultipartFile file) {
        try {
            studentService.importByExcel(file.getInputStream(), currentUserId());
            return new JsonResult<>(OK);
        } catch (IOException e) {
            log.error("IO异常", e);
            throw new SystemException("IO异常");
        }
    }

    @GetMapping("/my")
    public JsonResult<StudentDto> getMyStudentDto(){
        Integer studentId=currentUserId();
        StudentDto studentDto = studentService.my(studentId);
        return new JsonResult<>(OK, studentDto);
    }

    @GetMapping("/by_id")
    public JsonResult<StudentDto> getMyStudentDto(Integer studentId){
        StudentDto studentDto = studentService.my(studentId);
        return new JsonResult<>(OK, studentDto);
    }

    @PostMapping("/change_password")
    public JsonResult<Void> changePassword(@RequestBody ChangePassword changePassword){
        Integer studentId= currentUserId();
        studentService.changePassword(studentId, changePassword.getOldPassword(), changePassword.getNewPassword());
        log.info("{} changePassword",studentId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/delete")
    public JsonResult<List> deleteStudent(@RequestBody DeleteQo deleteQo){
        studentService.deleteStudentById(deleteQo.getStudentIds());
        Integer teacherId = currentUserId();
        List<StudentDto> students = studentService.findAllInStatusByTeacher(teacherId);
        return new JsonResult<>(OK, students);
    }
}
