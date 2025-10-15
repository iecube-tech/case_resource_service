package com.iecube.community.model.EMDV4Project.project.qo;

import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.project.entity.RemoteQo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EMDV4ProjectQo {
    private Integer caseId;
    private String projectName;
    private Integer useGroup;
    private Integer groupLimit;
    private Integer useRemote;
    private RemoteQo remoteQo;
    private Integer version;
    private String semester; // 学期
    private Integer gradeClass; // 班级
    private List<Date> date;
    private List<Student> students; // 空
    private List<Task> task;
    private List<ClassAndGrade> gradeClassList;
}
