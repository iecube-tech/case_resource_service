package com.iecube.community.model.student.controller;

import com.iecube.community.basecontroller.student.StudentBaseController;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.model.student.qo.DeleteQo;
import com.iecube.community.model.student.service.StudentService;
import com.iecube.community.model.teacher.qo.ChangePassword;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.ex.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController extends StudentBaseController {

    private static final String TEMPLATE_NAME = "学生导入模板.xls";
    @Value("${business.user.template.path}")
    private Resource userImportTemplate;

    @Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public JsonResult<List> findStudentsLimitByTeacherId(Integer page, Integer pageSize , HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<StudentDto> students = studentService.findStudentsLimitByTeacherId(teacherId, page, pageSize);
        return new JsonResult<>(OK, students);
    }

    @GetMapping("/num")
    public JsonResult<Integer> StudentNum(HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        Integer studentsNum = studentService.studentsNum(teacherId);
        return new JsonResult<>(OK, studentsNum);
    }

    @GetMapping("/all")
    public JsonResult<List> findAllInStatusByTeacher(HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<StudentDto> students = studentService.findAllInStatusByTeacher(teacherId);
        return new JsonResult<>(OK,students);
    }

    @PostMapping("/login")
    public  JsonResult<StudentDto> login(HttpSession session, String email, String password){
        StudentDto student = studentService.login(email, password);
        session.setAttribute("userid", student.getId());
        session.setAttribute("username", student.getStudentName());
        session.setAttribute("type", "student");
        log.info("login:{},{},{}", getUserTypeFromSession(session),getUserIdFromSession(session),getUsernameFromSession(session));
        return new JsonResult<>(OK, student);
    }

    @GetMapping("/logout")
    public JsonResult<Void> logout(HttpSession session){
        log.info("logout:{},{},{}",getUserTypeFromSession(session),getUserIdFromSession(session),getUsernameFromSession(session));
        session.invalidate();
        return new JsonResult<>(OK);
    }

    @PostMapping("/add")
    public JsonResult<Void> addStudent(@RequestBody AddStudentQo addStudentQo, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
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
    public JsonResult<Void> importByExcel(MultipartFile file, HttpSession session) {
        try {
            studentService.importByExcel(file.getInputStream(), getUserIdFromSession(session));
            return new JsonResult<>(OK);
        } catch (IOException e) {
            log.error("IO异常", e);
            throw new SystemException("IO异常");
        }
    }

    @GetMapping("/my")
    public JsonResult<StudentDto> getMyStudentDto(HttpSession session){
        Integer studentId=getUserIdFromSession(session);
        StudentDto studentDto = studentService.my(studentId);
        return new JsonResult<>(OK, studentDto);
    }

    @PostMapping("/change_password")
    public JsonResult<Void> changePassword(@RequestBody ChangePassword changePassword, HttpSession session){
        Integer studentId= getUserIdFromSession(session);
        studentService.changePassword(studentId, changePassword.getOldPassword(), changePassword.getNewPassword());
        log.info("{} changePassword",studentId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/delete")
    public JsonResult<List> deleteStudent(@RequestBody DeleteQo deleteQo, HttpSession session){
        studentService.deleteStudentById(deleteQo.getStudentIds());
        Integer teacherId = getUserIdFromSession(session);
        List<StudentDto> students = studentService.findAllInStatusByTeacher(teacherId);
        return new JsonResult<>(OK, students);
    }


}
