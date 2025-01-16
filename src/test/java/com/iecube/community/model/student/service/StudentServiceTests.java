package com.iecube.community.model.student.service;

import com.iecube.community.email.EmailParams;
import com.iecube.community.email.EmailSender;
import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.util.SHA256;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class StudentServiceTests {
    @Autowired StudentService studentService;

    @Value("${email.template.user-activate}")
    private Resource userActivateEmail;

    @Autowired
    private EmailSender emailSender;

    private static final String EMAIL_SUBJECT = "IECUBE-online数智化实验平台-新用户通知";

    @Test
    public void addStudent(){
        AddStudentQo addStudentQo = new AddStudentQo();
        addStudentQo.setStudentId("22408070133");
        addStudentQo.setStudentName("张小龙");
        addStudentQo.setEmail("1782352276@qq.com");
        addStudentQo.setStudentClass(1);
        addStudentQo.setMajorId(1);
        studentService.addStudent(addStudentQo, 6);
    }

    @Test
    public void passwordSHA256(){
        String pas = "111111";
        System.out.println(SHA256.encryptStringWithSHA256(pas));
    }

    @Test
    public void sendEmail(){
        List<EmailParams> toSendEmail = new ArrayList<>();
        toSendEmail.add(EmailParams.build(
                EMAIL_SUBJECT,
                this.buildText(this.userActivateEmail, "姓名", "password", "https://student.iecube.online/login"),
                "xiaolong.zhang@iecube.com.cn"
        ));
        emailSender.batchSend(toSendEmail);
    }

    private String buildText(Resource resource, Object... params) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileCopyUtils.copy(resource.getInputStream(), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = out.toString();
        text = MessageFormat.format(text, params);
        return text;
    }



}
