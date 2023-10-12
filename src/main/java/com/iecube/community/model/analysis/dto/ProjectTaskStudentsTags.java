package com.iecube.community.model.analysis.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectTaskStudentsTags {
    Integer taskNum;
    List<String> tags;
}
