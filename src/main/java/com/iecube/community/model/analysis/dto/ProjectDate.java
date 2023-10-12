package com.iecube.community.model.analysis.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDate {
    Integer NumberOfParticipant;
    Integer NumberOfCompleter;
    List<ProjectTaskStudentsGrade> projectTaskStudentsGradeList;
    List<ProjectTaskStudentsTags> projectTaskStudentsTagsList;
}
