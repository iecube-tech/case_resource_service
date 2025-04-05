package com.iecube.community.model.elaborate_md.lab_proc.qo;

import lombok.Data;

@Data
public class LabProcQo {
    private long id;
    private Long courseId;
    private String name;
    private String sectionPrefix; // ai知识库对应的章节序号
    private int sort;  // 服务端判断排序的数字
}
