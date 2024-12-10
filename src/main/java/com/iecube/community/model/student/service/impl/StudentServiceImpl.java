package com.iecube.community.model.student.service.impl;

import com.iecube.community.email.EmailParams;
import com.iecube.community.email.EmailSender;
import com.iecube.community.model.teacher.dto.LoginDto;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PasswordNotMatchException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.auth.service.ex.UserNotFoundException;
import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.major.entity.Collage;
import com.iecube.community.model.major.entity.Major;
import com.iecube.community.model.major.mapper.MajorMapper;
import com.iecube.community.model.major.service.MajorService;
import com.iecube.community.model.major.vo.CollageMajors;
import com.iecube.community.model.major.vo.SchoolCollageMajors;
import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.model.student.service.StudentService;
import com.iecube.community.model.student.service.ex.StudentDuplicateException;
import com.iecube.community.model.student.service.ex.StudentKeyFieldNotExistException;
import com.iecube.community.model.student.service.ex.StudentNotFoundException;
import com.iecube.community.model.student.service.ex.UnprocessableException;
import com.iecube.community.model.teacher.mapper.TeacherMapper;
import com.iecube.community.util.ex.SystemException;
import com.iecube.community.util.jwt.AuthUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    @Override
    public List<StudentDto> findStudentsLimitByTeacherId(Integer teacherId, Integer page, Integer pageSize) {
        List<StudentDto> students = studentMapper.findStudentsLimitByTeacher(teacherId, page, pageSize);
        if(students == null){
            throw new StudentNotFoundException("未找到数据");
        }
        return students;
    }

    @Override
    public Integer studentsNum(Integer teacherId) {
        Integer studentsNum = studentMapper.studentsNum(teacherId);
        return studentsNum;
    }

    @Override
    public List<StudentDto> findAllInStatusByTeacher(Integer teacherId) {
        List<StudentDto> students = studentMapper.findAllInStatusByTeacher(teacherId);
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
        Integer number = this.getRandomNumberInRange(8,16);
        String password = this.getRandomString(number);
        // sha256先加密 再使用md5 对sha256加密
        String sha256Password = SHA256.encryptStringWithSHA256(password);
        String md5Password = this.getMD5Password(sha256Password, addStudentDto.getSalt());
        addStudentDto.setPassword(md5Password);
        //保存
        this.saveStudent(addStudentDto);
        // 发送邮件
        this.sendStudentActiveEmail(addStudentDto.getStudentName(),addStudentDto.getEmail(),password);

    }

    /**
     * 返回一个密码为空的 AddStudentDto
     * @param addStudentQo
     * @param modifiedUser
     * @return
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
        if(student1.size() > 0){
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

    @Async
    public void sendStudentActiveEmail(String studentName, String studentEmail, String password) {
        String text = this.buildText(userActivateEmail, studentName, password, DomainName);
        emailSender.send(studentEmail, EMAIL_SUBJECT, text);
    }


    @Override
    public void importByExcel(InputStream in, Integer modifiedUser) {
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(in);
        } catch (IOException e) {
            log.error("IO异常", e);
            throw new SystemException("IO异常");
        }
        if (workbook.getNumberOfSheets() < 1) {
            throw new UnprocessableException("至少包含一个Sheet");
        }
        List<EmailParams> toSendEmail = new ArrayList<>();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
//            System.out.println("workbook.getNumberOfSheets()"+workbook.getNumberOfSheets());
//            System.out.println(" sheet.getLastRowNum()"+ sheet.getLastRowNum());
            // 解析row
            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    if (row.getLastCellNum() < 1) {
                        continue;
                    }
                    AddStudentDto addStudentDto = new AddStudentDto();
                    EnumMap<UserExcelHeaderEnum, String> rowData = this.getRowData(row);
                    addStudentDto.setStudentId(rowData.get(UserExcelHeaderEnum.NUM));
                    addStudentDto.setStudentName(rowData.get(UserExcelHeaderEnum.NAME));
                    addStudentDto.setEmail(rowData.get(UserExcelHeaderEnum.EMAIL));
                    Integer majorId = this.getMajorId(rowData.get(UserExcelHeaderEnum.COLLAGE),rowData.get(UserExcelHeaderEnum.MAJOR), modifiedUser);
                    addStudentDto.setMajorId(majorId);
                    Integer gradeClassId = this.getGradeClassId(rowData.get(UserExcelHeaderEnum.GRADE_CLASS),rowData.get(UserExcelHeaderEnum.GRADE), majorId, modifiedUser);
                    addStudentDto.setStudentClass(gradeClassId);
                    addStudentDto.setStatus(1);
                    addStudentDto.setCreator(modifiedUser);
                    addStudentDto.setCreateTime(new Date());
                    addStudentDto.setLastModifiedUser(modifiedUser);
                    addStudentDto.setLastModifiedTime(new Date());
                    String salt = UUID.randomUUID().toString().toUpperCase();
                    addStudentDto.setSalt(salt);
                    Integer number = this.getRandomNumberInRange(8,16);
                    String password = this.getRandomString(number);
                    String sha256Password = SHA256.encryptStringWithSHA256(password);
                    String md5Password = this.getMD5Password(sha256Password, addStudentDto.getSalt());
                    addStudentDto.setPassword(md5Password);
                    this.saveStudent(addStudentDto);
                    toSendEmail.add(EmailParams.build(
                            EMAIL_SUBJECT,
                            this.buildText(this.userActivateEmail, addStudentDto.getStudentName(), password),
                            addStudentDto.getEmail()
                    ));
                }
            }
        }
        this.sendEmail(toSendEmail);
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
        if(students==null || students.size()<1){
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

    @Async
    public void sendEmail(List<EmailParams> list){
        emailSender.batchSend(list);
    }

    private Integer getMajorId(String collageName,String majorName, Integer teacherId){
        Integer majorId = null;
        // 判断是否和老师的学院相同
        SchoolCollageMajors schoolCollageMajors = majorService.getSchoolCollageMajorsByTeacher(teacherId);
        List<CollageMajors> collagesMajors =  schoolCollageMajors.getCollageMajorsList();
        List<CollageMajors> thisStudentCollage = new ArrayList<>();
        for (CollageMajors collageMajors : collagesMajors){
            if(collageMajors.getCollageName().equals(collageName)){
                thisStudentCollage.add(collageMajors);
                break;
            }
        }
        if(thisStudentCollage.size()==0){
            // 学校的学院列表中没有新增的学生的学院
            // 创建学院 创建专业
            Integer collageId = this.createCollage(collageName, schoolCollageMajors.getSchoolId());
            majorId = this.createMajor(majorName, collageId,teacherId);
        }else{
            // 学院的专业列表
            List<Major> thisCollageMajors = thisStudentCollage.get(0).getMajorList();
            List<Major> thisStudentMajor = new ArrayList<>();
            for(Major major : thisCollageMajors){
                if(major.getName().equals(majorName)){
                    thisStudentMajor.add(major);
                    break;
                }
            }
            if(thisStudentMajor.size()==0){
                // 专业列表中没有该学生的专业
                // 创建专业
                majorId = this.createMajor(majorName,thisStudentCollage.get(0).getCollageId(), teacherId);
            }else{
                majorId =  thisStudentMajor.get(0).getId();
            }
        }
        return majorId;
    }

    private Integer createMajor(String majorName, Integer collageId, Integer teacherId){
        Major major = new Major();
        major.setName(majorName);
        major.setCreator(teacherId);
        major.setCollageId(collageId);
        major.setLastModifiedUser(teacherId);
        major.setCreateTime(new Date());
        major.setLastModifiedTime(new Date());
        majorMapper.addMajor(major);
        Integer majorId = major.getId();
        return majorId;
    }

    private Integer createCollage(String collageName, Integer schoolId){
        Collage collage = new Collage();
        collage.setName(collageName);
        collage.setSchoolId(schoolId);
        majorMapper.addCollage(collage);
        Integer collageId = collage.getId();
        return collageId;
    }

    private Integer createGradeClass(String classGrade, String className, Integer majorId, Integer teacherId ){
        ClassAndGrade classAndGrade = new ClassAndGrade();
        classAndGrade.setGrade(classGrade);
        classAndGrade.setName(className);
        classAndGrade.setMajorId(majorId);
        classAndGrade.setCreator(teacherId);
        classAndGrade.setCreateTime(new Date());
        classAndGrade.setLastModifiedUser(teacherId);
        classAndGrade.setLastModifiedTime(new Date());
        majorMapper.addGradeClass(classAndGrade);
        Integer gradeClassId = classAndGrade.getId();
        return gradeClassId;
    }

    private Integer getGradeClassId(String className, String classGrade, Integer majorId, Integer teacherId){
        Integer gradeClassId = majorMapper.MajorClassId(classGrade,className,majorId);
        if (gradeClassId==null){
            gradeClassId = this.createGradeClass(classGrade,className,majorId, teacherId);
        }
        return gradeClassId;
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
        return cell.getStringCellValue();
    }

    protected enum UserExcelHeaderEnum {
        /**
         * 编号
         */
        NUM("学号"),
        NAME("名称"),
        EMAIL("邮箱"),
        COLLAGE("学院"),
        MAJOR("专业"),
        GRADE("年级"),
        GRADE_CLASS("班级");
        @Getter
        private final String remark;

        UserExcelHeaderEnum(String remark) {
            this.remark = remark;
        }
    }
}
