package com.iecube.community.model.project.entity;

import com.iecube.community.model.task.entity.ProjectStudentTask;
import com.iecube.community.model.task.entity.StudentTaskVo;
import lombok.Data;

import java.util.List;

/**
 * 教师端 学生端查询学生任务
 */
@Data
public class ProjectStudentVo {
    private Integer psId; // project_student 的id
    private Integer id;  //学生信息表的id
    private String studentId; // 学号
    private String studentName;
    private Double studentGrade;
    private List<StudentTaskVo> studentTasks;
    private List<String> suggestion;
}
