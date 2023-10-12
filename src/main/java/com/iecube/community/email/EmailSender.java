package com.iecube.community.email;

import com.iecube.community.util.ex.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 邮件发送器
 *
 * @author panghaoyue
 */
@Component
public class EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void batchSend(List<EmailParams> paramsList) {
        if (!CollectionUtils.isEmpty(paramsList)) {
            for (EmailParams emailParams : paramsList) {
                this.send(emailParams.getReceivers(), emailParams.getSubject(), emailParams.getText());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    LOGGER.error("线程唤醒异常", e);
                }
            }
        }
    }

    @Async
    public void send(String receiver, String subject, String text) {
        this.send(Collections.singletonList(receiver), subject, text);
    }

    @Async
    public void send(List<String> receivers, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
        } catch (MessagingException e) {
            LOGGER.error("构建邮件消息失败", e);
            throw new SystemException();
        }
        try {
            helper.setFrom(mailFrom);
        } catch (MessagingException e) {
            LOGGER.error("设置发信人失败", e);
            throw new SystemException();
        }
        for (String receiver : receivers) {
            try {
                helper.addTo(receiver);
            } catch (MessagingException e) {
                LOGGER.error("设置收信人失败", e);
                throw new SystemException();
            }
        }
        try {
            helper.setSubject(subject);
        } catch (MessagingException e) {
            LOGGER.error("设置主题失败", e);
            throw new SystemException();
        }
        try {
            helper.setText(text, true);
        } catch (MessagingException e) {
            LOGGER.error("设置正文失败", e);
            throw new SystemException();
        }
        Date sendTime = new Date();
        LOGGER.warn("开始发送邮件，发送时间：{}，接收人：{}，主题：{}，正文：{}", sendTime, receivers, subject, text);
        javaMailSender.send(mimeMessage);
        LOGGER.warn("发送完成，发送时间：{}，完成时间：{}", sendTime, new Date());
    }

}
