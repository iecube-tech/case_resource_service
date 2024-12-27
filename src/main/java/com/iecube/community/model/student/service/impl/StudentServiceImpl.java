package com.iecube.community.model.student.service.impl;

import com.iecube.community.email.EmailParams;
import com.iecube.community.email.EmailSender;
import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.Auth;
import com.iecube.community.model.auth.service.AuthService;
import com.iecube.community.model.major.entity.School;
import com.iecube.community.model.student.dto.LoginDto;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PasswordNotMatchException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.auth.service.ex.UserNotFoundException;
import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.major.entity.Collage;
import com.iecube.community.model.major.entity.Major;
import com.iecube.community.model.major.mapper.MajorMapper;
import com.iecube.community.model.major.service.MajorService;
import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.model.student.service.StudentService;
import com.iecube.community.model.student.service.ex.*;
import com.iecube.community.model.teacher.mapper.TeacherMapper;
import com.iecube.community.util.ex.SystemException;
import com.iecube.community.util.jwt.AuthUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.iecube.community.util.SHA256;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {


    private static final String EMAIL_SUBJECT = "IECUBE产业案例资教学资源库和过程评价系统-新用户通知";

    @Value("${email.template.user-activate}")
    private Resource userActivateEmail;

    @Value("${DomainName.student}")
    private String DomainName;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private MajorService majorService;

    @Autowired
    private EmailSender emailSender;

    @Value("${password.default-enable}")
    private Boolean passwordDefaultEnable;

    @Value("${password.student}")
    private String defaultPassword;

    @Autowired
    private AuthService authService;

    @Override
    public List<StudentDto> findStudentsLimitByTeacherId(Integer teacherId, Integer page, Integer pageSize) {
        Boolean AS = authService.havaAuth(teacherId, Auth.AS.getAuth());
        List<StudentDto> students = new ArrayList<>();
        if(AS){
            students = studentMapper.findAllStudentLimit(page, pageSize);
        }else {
            students = studentMapper.findStudentsLimitByTeacher(teacherId, page, pageSize);
        }
        if(students == null){
            throw new StudentNotFoundException("未找到数据");
        }
        return students;
    }

    @Override
    public Integer studentsNum(Integer teacherId) {
        Boolean AS = authService.havaAuth(teacherId, Auth.AS.getAuth());
        if(AS){
            return studentMapper.allStudentNum();
        }
        return studentMapper.studentsNum(teacherId);
    }

    @Override
    public List<StudentDto> findAllInStatusByTeacher(Integer teacherId) {
        Boolean AS = authService.havaAuth(teacherId, Auth.AS.getAuth());
        List<StudentDto> students = new ArrayList<>();
        if(AS){
            students = studentMapper.findAllStudent();
        }else{
            students = studentMapper.findAllInStatusByTeacher(teacherId);
        }
        if (students==null){
            throw new StudentNotFoundException("没有学生数据");
        }
        return students;
    }

    @Override
    public void changePassword(Integer studentId, String oldPassword, String newPassword) {
        Student result = studentMapper.getStudentById(studentId);
        if(result == null){
            throw new UserNotFoundException("用户未找到");
        }
        String salt = result.getSalt();
        String oldMd5Password = result.getPassword();
        String checkMd5Password = getMD5Password(oldPassword, salt);

        if (!checkMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("原用户密码错误");
        }
        String newMd5Password = getMD5Password(newPassword,salt);
        Integer row = studentMapper.changePassword(studentId,newMd5Password);
        if(row != 1){
            throw new UpdateException("更新密码异常");
        }
    }

    @Override
    public StudentDto my(Integer studentId) {
        StudentDto student = studentMapper.getById(studentId);
        if(student == null) {
            throw new StudentNotFoundException("用户不存在");
        }
        return student;
    }

    @Override
    public void addStudent(AddStudentQo addStudentQo, Integer teacherId) {
        AddStudentDto addStudentDto = this.initAddStudentDto(addStudentQo,teacherId);
        int number = getRandomNumberInRange(8,16);
        String password = this.getRandomString(number);
        // sha256先加密 再使用md5 对sha256加密
        String sha256Password = SHA256.encryptStringWithSHA256(password);
        String md5Password = getMD5Password(sha256Password, addStudentDto.getSalt());
        addStudentDto.setPassword(md5Password);
        //保存
        this.saveStudent(addStudentDto);
        // 发送邮件
        this.sendStudentActiveEmail(addStudentDto.getStudentName(),addStudentDto.getEmail(),password);

    }

    @Override
    public List<AddStudentDto> importByExcel(InputStream in, Integer modifiedUser) {
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(in);
        } catch (IOException e) {
            log.error("IO异常", e);
            throw new SystemException("IO异常");
        }
        if (workbook.getNumberOfSheets() < 1) {
            throw new UnprocessableException("至少包含一个Sheet");
        }
        List<EmailParams> toSendEmail = new ArrayList<>();
        List<AddStudentDto> failedList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        // 解析row
        for (int rowNum = 3; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                if (row.getLastCellNum() < 1) {
                    continue;
                }
                AddStudentDto addStudentDto = new AddStudentDto();
                EnumMap<UserExcelHeaderEnum, String> rowData = this.getRowData(row);
                String studentId = rowData.get(UserExcelHeaderEnum.NUM);
                String studentName = rowData.get(UserExcelHeaderEnum.NAME);
                String email = rowData.get(UserExcelHeaderEnum.EMAIL);
                String school = rowData.get(UserExcelHeaderEnum.SCHOOL);
                String collage = rowData.get(UserExcelHeaderEnum.COLLAGE);
                String major = rowData.get(UserExcelHeaderEnum.MAJOR);
                Integer grade = Integer.valueOf(rowData.get(UserExcelHeaderEnum.GRADE));
                String gradeClass = rowData.get(UserExcelHeaderEnum.GRADE_CLASS);
                // schoolId
                Integer schoolId = this.getSchoolId(school);
                Integer collageId = this.getCollageId(collage, schoolId, modifiedUser);
                Integer majorId = this.getMajorId(major, collageId, modifiedUser);
                Integer gradeClassId = this.getGradeClassId(grade, gradeClass, majorId, modifiedUser);
                addStudentDto.setMajorId(majorId);
                addStudentDto.setStudentClass(gradeClassId);
                addStudentDto.setStudentId(studentId);
                addStudentDto.setStudentName(studentName);
                addStudentDto.setEmail(email);
                addStudentDto.setStatus(1);
                addStudentDto.setCreator(modifiedUser);
                addStudentDto.setCreateTime(new Date());
                addStudentDto.setLastModifiedUser(modifiedUser);
                addStudentDto.setLastModifiedTime(new Date());
                String salt = UUID.randomUUID().toString().toUpperCase();
                addStudentDto.setSalt(salt);
                String password = defaultPassword;
                if(!passwordDefaultEnable){
                    password = this.getRandomString(getRandomNumberInRange(8,16));
                }
                String sha256Password = SHA256.encryptStringWithSHA256(password);
                String md5Password = getMD5Password(sha256Password, addStudentDto.getSalt());
                addStudentDto.setPassword(md5Password);
                try{
                    this.saveStudent(addStudentDto);
                }catch (StudentDuplicateException e){
                    failedList.add(addStudentDto);
                }
                if(!passwordDefaultEnable) {
                    toSendEmail.add(EmailParams.build(
                            EMAIL_SUBJECT,
                            this.buildText(this.userActivateEmail, addStudentDto.getStudentName(), password),
                            addStudentDto.getEmail()
                    ));
                }
            }
        }
        this.sendEmail(toSendEmail);
        return failedList;
    }

    @Override
    public void deleteStudentById(List<Integer> studentIds) {
        for(Integer id : studentIds){
            studentMapper.deleteStudent(id);
        }
    }

    @Override
    public LoginDto jwtLogin(String email, String password) {
        List<Student> students = studentMapper.getByEmail(email);
        if(students==null || students.isEmpty()){
            throw new StudentNotFoundException("用户不存在");
        }
        Student student = students.get(0);
        if (student.getStatus()==0){
            throw new StudentNotFoundException("用户不存在");
        }
        // 检测密码
        // 先获取数据库加密后的密码 盐值  和用户传递过来的密码(相同的方法进行加密)进行比较
        String salt = student.getSalt();
        String oldMd5Password = student.getPassword();
        String newMd5Password = getMD5Password(password, salt);
        if (!newMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("用户密码错误");
        }
        StudentDto studentDto = studentMapper.getById(student.getId());
        LoginDto loginDto = new LoginDto();
        loginDto.setStudentDto(studentDto);
        loginDto.setToken(new AuthUtils().createToken(studentDto.getId(), studentDto.getEmail(), "student"));
        return loginDto;
    }

    @Override
    public void sendSignInCodeToEmail(String email, StringRedisTemplate stringRedisTemplate) {
        if( email == null || email.isEmpty()){
            throw new ParameterException("参数异常", new Exception("参数email错误"));
        }
        // 验证是否已注册
        List<Student> students = studentMapper.getByEmail(email);
        if(!(students==null || students.isEmpty())){
            throw new StudentDuplicateException("用户已存在");
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int code  = random.nextInt(100000, 1000000);
        stringRedisTemplate.opsForValue().set("CODE_SIGN_IN_"+email, String.valueOf(code), 10, TimeUnit.MINUTES);
        emailSender.send(email, "【北京曾益慧创--IECUBEOnline】验证码 "+code,"您正在进行注册验证，\n 验证码为："+code+"\n此验证码15分钟有效。\n");
    }

    public void verifySignInCode(String email, String code, StringRedisTemplate stringRedisTemplate) {
        String emailSignInCode = stringRedisTemplate.opsForValue().get("CODE_SIGN_IN_"+email);
        if(emailSignInCode==null || !emailSignInCode.equals(code)){
            throw new VerifyCodeFailed("验证码校验不通过");
        }
    }

    /**
     * 返回一个密码为空的 AddStudentDto
     */
    public AddStudentDto initAddStudentDto(AddStudentQo addStudentQo, Integer modifiedUser){
        AddStudentDto addStudentDto = new AddStudentDto();
        addStudentDto.setEmail(addStudentQo.getEmail());
        addStudentDto.setStudentId(addStudentQo.getStudentId());
        addStudentDto.setStudentName(addStudentQo.getStudentName());
        addStudentDto.setStudentClass(addStudentQo.getStudentClass());
        addStudentDto.setMajorId(addStudentQo.getMajorId());
        addStudentDto.setStatus(1);
        addStudentDto.setCreator(modifiedUser);
        addStudentDto.setCreateTime(new Date());
        addStudentDto.setLastModifiedTime(new Date());
        addStudentDto.setLastModifiedUser(modifiedUser);
        String salt = UUID.randomUUID().toString().toUpperCase();
        addStudentDto.setSalt(salt);
        return addStudentDto;
    }

    public void saveStudent(AddStudentDto addStudentDto){
        //检查关键字符是否存在
        if(addStudentDto.getStudentName() == null){
            throw new StudentKeyFieldNotExistException( addStudentDto.getStudentId()+"学生姓名不能为空");
        }
        if(addStudentDto.getStudentId() == null){
            throw new StudentKeyFieldNotExistException(addStudentDto.getStudentName()+"学生学号不能为空");
        }
        if(addStudentDto.getEmail() == null){
            throw new StudentKeyFieldNotExistException(addStudentDto.getStudentName()+"学生邮箱不能为空");
        }

        if(addStudentDto.getMajorId() == null){
            throw new StudentKeyFieldNotExistException(addStudentDto.getStudentName()+"学生专业信息不能为空");
        }
        //判断是否和已有冲突
//        System.out.println(addStudentDto);
        List<Student> student1 = studentMapper.getByEmail(addStudentDto.getEmail());
//        System.out.println(studentMapper.getByEmail(addStudentDto.getEmail()));
        if(!student1.isEmpty()){
            throw new StudentDuplicateException(addStudentDto.getEmail()+"邮箱已存在");
        }
        Student student = studentMapper.getByStudentId(addStudentDto.getStudentId());
        if(student != null){
            throw new StudentDuplicateException(addStudentDto.getStudentId()+"学号已存在");
        }

        Integer row = studentMapper.addStudent(addStudentDto);
        if (row!=1){
            throw new InsertException("添加数据异常");
        }
    }

    /**定义一个md5算法加密**/
    private static String getMD5Password(String password, String salt){
        // md5加密算法的方法 进行3次
        for (int i=0; i<3; i++){
            password = DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
    }

    //length用户要求产生字符串的长度
    private String getRandomString(int length){
        if(passwordDefaultEnable){
            return defaultPassword;
        }
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
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

    @Async
    public void sendStudentActiveEmail(String studentName, String studentEmail, String password) {
        String text = this.buildText(userActivateEmail, studentName, password, DomainName);
        emailSender.send(studentEmail, EMAIL_SUBJECT, text);
    }

    @Async
    public void sendEmail(List<EmailParams> list){
        emailSender.batchSend(list);
    }

    private  Integer getSchoolId(String name){
        School school = majorMapper.getSchoolByName(name);
        if(Objects.isNull(school)){
            School newSchool = new School();
            newSchool.setName(name);
            int res = majorMapper.addSchool(newSchool);
            if(res==1){
                return newSchool.getId();
            }else throw new InsertException("服务器更新数据错误");
        }else {
            return school.getId();
        }
    }

    private Integer getCollageId(String name, Integer schoolId, Integer modifiedUser){
        List<Collage> collageList = majorMapper.getCollagesBySchool(schoolId);
        for (Collage collage : collageList) {
            if (collage.getName().equals(name)) {
                return collage.getId();
            }
        }
        Collage newCollage = new Collage();
        newCollage.setName(name);
        newCollage.setSchoolId(schoolId);
        int res = majorMapper.addCollage(newCollage);
        if(res!=1){
            throw new InsertException("服务器更新数据错误");
        }
        return newCollage.getId();
    }

    private Integer getMajorId(String major,Integer collageId, Integer modifyUser){
        List<Major> majorList = majorMapper.getMajorsByCollage(collageId);
        for(Major major1 : majorList){
            if(major1.getName().equals(major)){
                return major1.getId();
            }
        }
        Major newMajor = new Major();
        newMajor.setCollageId(collageId);
        newMajor.setName(major);
        newMajor.setCreator(modifyUser);
        newMajor.setLastModifiedUser(modifyUser);
        newMajor.setCreateTime(new Date());
        newMajor.setLastModifiedTime(new Date());
        int res = majorMapper.addMajor(newMajor);
        if(res!=1){
            throw new InsertException("服务器更新数据错误");
        }
        return newMajor.getId();
    }

    private Integer createGradeClass(Integer classGrade, String className, Integer majorId, Integer teacherId ){
        ClassAndGrade classAndGrade = new ClassAndGrade();
        classAndGrade.setGrade(classGrade);
        classAndGrade.setName(className);
        classAndGrade.setMajorId(majorId);
        classAndGrade.setCreator(teacherId);
        classAndGrade.setCreateTime(new Date());
        classAndGrade.setLastModifiedUser(teacherId);
        classAndGrade.setLastModifiedTime(new Date());
        majorMapper.addGradeClass(classAndGrade);
        return classAndGrade.getId();
    }

    private Integer getGradeClassId(Integer grade, String className, Integer majorId, Integer teacherId){
        List<ClassAndGrade> classGradeList = majorMapper.getClassesByMajorId(majorId);
        for(ClassAndGrade classAndGrade : classGradeList){
            if(classAndGrade.getName().equals(className) && classAndGrade.getGrade().equals(grade)){
                return classAndGrade.getId();
            }
        }
        return  createGradeClass(grade, className, majorId, teacherId);
    }

    private EnumMap<UserExcelHeaderEnum, String> getRowData(Row row) {
        EnumMap<UserExcelHeaderEnum, String> map = new EnumMap<>(UserExcelHeaderEnum.class);
        for (UserExcelHeaderEnum col : UserExcelHeaderEnum.values()) {
            Cell cell = row.getCell(col.ordinal());
            String value = this.getStringValue(cell);
            map.put(col, value);
        }
        return map;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        if(cell.getCellType().equals(CellType.NUMERIC)){
            return String.valueOf((int)cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }

    @Getter
    protected enum UserExcelHeaderEnum {
        /**
         * 编号
         */
        SCHOOL("学校"),
        COLLAGE("学院"),
        MAJOR("专业"),
        GRADE("年级"),
        GRADE_CLASS("班级"),
        NUM("学号"),
        NAME("姓名"),
        EMAIL("邮箱");
        private final String remark;

        UserExcelHeaderEnum(String remark) {
            this.remark = remark;
        }
    }
}
