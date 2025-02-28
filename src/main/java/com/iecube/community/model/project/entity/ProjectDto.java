package com.iecube.community.model.project.entity;

import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.student.entity.Student;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDto {
    private Integer caseId;
    private String projectName;
    private Integer useGroup;
    private Integer groupLimit;
    private Integer useRemote;
    private RemoteQo remoteQo;
    private List<Date> date;
    private List<Student> students;
    private List<Task> task;
}
