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
    Integer psId; // project_student 的id
    Integer id;  //学生信息表的id
    String studentId; // 学号
    String studentName;
    Integer studentGrade;
    List<StudentTaskVo> studentTasks;
    List<String> suggestion;
}
