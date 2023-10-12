package com.iecube.community.model.analysis.vo;

import com.iecube.community.model.analysis.dto.*;
import com.iecube.community.util.ListCounter;
import lombok.Data;

import java.util.List;

@Data
public class CurrentProjectData {
    Integer numberOfParticipant;
    Integer numberOfCompleter;
    double AverageGrade;
    List<PersonnelDistribution> PersonnelDistributions;
    List<TaskAverage> TaskAverages;
    List<TaskMedian> TaskMedians;
    List<ProjectTaskStudentsGrade> projectTaskStudentsGradeList;
    List<ProjectTaskStudentsTags> projectTaskStudentsTagsList;
    List<ListCounter.Occurrence> tagsCount;
}
