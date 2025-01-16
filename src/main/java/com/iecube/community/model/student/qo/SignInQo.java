package com.iecube.community.model.student.qo;

import lombok.Data;

@Data
public class SignInQo {
    private String email;
    private String password;
    private String rePassword;
    private String code;
}
