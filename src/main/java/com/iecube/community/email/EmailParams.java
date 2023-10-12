package com.iecube.community.email;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 邮箱参数模型
 *
 * @author panghaoyue
 */
@Data
public class EmailParams {

    private List<String> receivers;
    private String subject;
    private String text;

    public static EmailParams build(String subject, String text, String... receivers) {
        EmailParams emailParams = new EmailParams();
        if (receivers != null && receivers.length > 0) {
            emailParams.setReceivers(Arrays.asList(receivers));
        }
        emailParams.setSubject(subject);
        emailParams.setText(text);
        return emailParams;
    }
}
