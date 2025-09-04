package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.qo;

import com.iecube.community.model.student.entity.Student;
import lombok.Data;

import java.util.List;

@Data
public class TaskGroupQo {
    private Long id;
    private Long taskId;
    private String name;
    private List<Student> students;
}
