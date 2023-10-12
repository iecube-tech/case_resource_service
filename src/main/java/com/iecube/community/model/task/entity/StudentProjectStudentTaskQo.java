package com.iecube.community.model.task.entity;

import lombok.Data;

import java.util.List;

/**
 * 用于学生端提交PST内容
 *
 */
@Data
public class StudentProjectStudentTaskQo {
    List<String> imgs;
    List<String> files;
    String content;
}
