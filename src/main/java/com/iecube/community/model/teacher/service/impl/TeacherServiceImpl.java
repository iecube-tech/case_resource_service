package com.iecube.community.model.teacher.service.impl;

import com.iecube.community.email.EmailSender;
import com.iecube.community.model.teacher.dto.LoginDto;
import com.iecube.community.model.auth.service.ex.*;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.mapper.ContentMapper;
import com.iecube.community.model.major.mapper.MajorMapper;
import com.iecube.community.model.major.vo.SchoolCollage;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.mapper.TeacherMapper;
import com.iecube.community.model.teacher.service.TeacherService;
import com.iecube.community.model.teacher.vo.TeacherVo;
import com.iecube.community.util.SHA256;
import com.iecube.community.util.jwt.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Value("${password.default-enable}")
    private Boolean passwordDefaultEnable;

    @Value("${password.teacher}")
    private String defaultPassword;

    @Value("${email.template.teacher-activate}")
    private Resource userActivateEmail;

    @Value("${DomainName.teacher}")
    private String DomainName;

    @Autowired
    private EmailSender emailSender;

    private static final String EMAIL_SUBJECT = "IECUBE产业案例资教学资源库和过程评价系统-新用户通知";

    @Override
    public LoginDto login(String email, String password) {
        Teacher result = teacherMapper.findByEmail(email);
        if(result == null){
            throw new UserNotFoundException("用户未找到");
        }
        if(result.getIsDelete() == 1){
            throw new UserNotFoundException("用户不可用");
        }
        // 检测密码
        // 先获取数据库加密后的密码 盐值  和用户传递过来的密码(相同的方法进行加密)进行比较
        String salt = result.getSalt();
        String oldMd5Password = result.getPassword();
        String newMd5Password = getMD5Password(password, salt);
        if (!newMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("用户密码错误");
        }
        result.setPassword(null);
        result.setSalt(null);
        LoginDto loginDto = new LoginDto();
        loginDto.setTeacher(result);
        loginDto.setToken(new AuthUtils().createToken(result.getId(), result.getEmail(), "teacher"));
        return loginDto;
    }

    @Override
    public void insert(Teacher user) {
        // 通过user参数来获取传递过来的Email
        // 调用findByPhoneNum(PhoneNum) 判断用户是否被注册
        Teacher result = teacherMapper.findByEmail(user.getEmail());
        //  判断结果集是否为null 不为null 则抛出用户已存在的异常
        if (result != null){
            //抛出异常
            throw new EmailDuplicateException("该邮箱已存在。");
        }
        // 补全注册数据 is_delete 4个日志(谁操作)
        user.setIsDelete(0);
        // 密码加密处理 md5 加密算法
        // （串 + password + 串）  全部交给md5加密 连续加载3次
        // 盐值 + password + 盐值 盐值就是一个随机的字符串
        Integer number = this.getRandomNumberInRange(8,16);
        String password = this.getRandomString(number);
        String sha256Password = SHA256.encryptStringWithSHA256(password);
        // 获取盐值（随机生成一个盐值）
        String salt = UUID.randomUUID().toString().toUpperCase();
        //将密码和盐值作为一个整体进行加密处理
        String md5Password = getMD5Password(sha256Password, salt);
        // 将加密后的密码重新补全到user中去
        user.setPassword(md5Password);
        user.setSalt(salt);
        // 执行注册业务逻辑
        Integer rows = teacherMapper.insert(user);
        if (rows != 1){
            throw new InsertException("在用户注册过程中产生未知异常");
        }
        if(!passwordDefaultEnable){
            this.sendActiveEmail(user.getUsername(), user.getEmail(),password);
        }

    }

    @Async
    public void sendActiveEmail(String teacherName, String teacherEmail, String password) {
        String text = this.buildText(userActivateEmail, teacherName, password, DomainName);
//        System.out.println(text);
        emailSender.send(teacherEmail, EMAIL_SUBJECT, text);
    }

    private String getRandomString(int length){
        if(passwordDefaultEnable){
            return defaultPassword;
        }
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    //生成指定区间的随机数
    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private String buildText(Resource resource, Object... params) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileCopyUtils.copy(resource.getInputStream(), out);
        } catch (IOException e) {
            log.error("IO异常", e);
        }
        String text = out.toString();
        text = MessageFormat.format(text, params);
        return text;
    }

    @Override
    public void changePassword(Integer teacherId, String oldPassword, String newPassword) {
        Teacher result = teacherMapper.findById(teacherId);
        if(result == null){
            throw new UserNotFoundException("用户未找到");
        }
        if(result.getIsDelete() == 1){
            throw new UserNotFoundException("用户数据不存在");
        }
        String salt = result.getSalt();
        String oldMd5Password = result.getPassword();
        String checkMd5Password = getMD5Password(oldPassword, salt);
        if (!checkMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("原用户密码错误");
        }

        String newMd5Password = getMD5Password(newPassword,salt);
        Integer row = teacherMapper.changePassword(newMd5Password, teacherId);
        if(row != 1){
            throw new UpdateException("更新密码异常");
        }
    }

    @Override
    public Teacher teacherAccount(Integer teacherId) {
        Teacher teacher = teacherMapper.findById(teacherId);
        teacher.setPassword(null);
        teacher.setSalt(null);
        return teacher;
    }

    @Override
    public List<TeacherVo> getTeacherVoList() {
        List<Teacher> teachers = teacherMapper.findAll();
        List<TeacherVo> teacherVoList = new ArrayList<>();
        for(Teacher teacher : teachers){
            TeacherVo teacherVo = this.TeacherVoGeneration(teacher);
            teacherVoList.add(teacherVo);
        }
        return teacherVoList;
    }

    private TeacherVo TeacherVoGeneration(Teacher teacher){
        TeacherVo teacherVo = new TeacherVo();
        teacherVo.setTeacherId(teacher.getId());
        teacherVo.setTeacherName(teacher.getUsername());
        teacherVo.setTeacherEmail(teacher.getEmail());
        teacherVo.setIsDelete(teacher.getIsDelete());
        teacherVo.setCreatorId(teacher.getCreator());
        teacherVo.setCollageId(teacher.getCollageId());
        SchoolCollage schoolCollage = majorMapper.SchoolCollage(teacher.getCollageId());
        teacherVo.setCollageName(schoolCollage.getCollageName());
        teacherVo.setSchoolId(schoolCollage.getSchoolId());
        teacherVo.setSchoolName(schoolCollage.getSchoolName());
        Teacher creator = teacherMapper.findById(teacher.getCreator());
        teacherVo.setCreatorName(creator.getUsername());
        List<Content> contentList = contentMapper.findByTeacherId(teacher.getId());
        teacherVo.setCaseList(contentList);
        return teacherVo;
    }

    @Override
    public TeacherVo teacherInfo(Integer teacherId) {
        Teacher teacher = teacherMapper.findById(teacherId);
        TeacherVo teacherVo = this.TeacherVoGeneration(teacher);
        return teacherVo;
    }

    @Override
    public void teacherEnable(Integer teacherId) {
        teacherMapper.teacherEnable(teacherId);
    }

    @Override
    public void teacherDisable(Integer teacherId) {
        teacherMapper.teacherDisable(teacherId);
    }

    @Override
    public List<Teacher> collageTeachers(Integer teacherId) {
        List<Teacher> teacherList = teacherMapper.collageTeachers(teacherId);
        return teacherList;
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
}
