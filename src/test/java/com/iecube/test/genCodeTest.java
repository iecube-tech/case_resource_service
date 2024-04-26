package com.iecube.test;

import org.springframework.util.DigestUtils;

import java.util.Date;

public class genCodeTest {
    public static void main(String[] args) {
        int id = 4;
        Date date = new Date();
        String re = DigestUtils.md5DigestAsHex((id+"-"+date.getTime()).getBytes()).toUpperCase();
    }

}
