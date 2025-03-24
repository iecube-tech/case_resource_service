package com.iecube.community.model.elaborate_md.lab_model.qo;

import lombok.Data;

@Data
public class LabModelQo {
    private long id;
    private Long labProcId;
    private String name;
    private String icon;
    private int sort;  // 服务端判断排序的数字
}
