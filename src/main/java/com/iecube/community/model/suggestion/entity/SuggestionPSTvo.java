package com.iecube.community.model.suggestion.entity;

import com.iecube.community.model.task.entity.StudentTaskVo;
import lombok.Data;

import java.util.List;

@Data
public class SuggestionPSTvo {
    Integer id;  //学生信息表的id
    String studentId; // 学号
    String studentName;
    Integer studentGrade;
    List<StudentTaskVo> studentTasks;
    List<String> suggestions;
}
