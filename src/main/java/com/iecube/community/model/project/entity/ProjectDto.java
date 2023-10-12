package com.iecube.community.model.project.entity;

import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.student.entity.Student;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDto {
    Integer caseId;
    String projectName;
    List<Date> date;
    List<Student> students;
    List<Task> task;
}
