package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity;

import com.iecube.community.model.student.entity.Student;
import lombok.Data;

import java.util.List;

@Data
public class EMDV4TaskGroup {
    private Long id;
    private String name;
    private Long emdv4TaskId;
    private Integer limitNum;
    private Integer status;
    private String code;
    private Integer creator;
    private Boolean isCreator;
    private List<Student> studentList;
}
