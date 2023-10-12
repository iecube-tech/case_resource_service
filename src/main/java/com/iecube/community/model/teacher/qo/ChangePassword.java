package com.iecube.community.model.teacher.qo;

import lombok.Data;

@Data
public class ChangePassword {
    String oldPassword;
    String newPassword;
}
