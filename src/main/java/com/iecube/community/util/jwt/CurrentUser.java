package com.iecube.community.util.jwt;

import lombok.Data;

@Data
public class CurrentUser {
    Integer id;
    String email;
    String userType;
}
