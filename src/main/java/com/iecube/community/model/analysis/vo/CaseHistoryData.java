package com.iecube.community.model.analysis.vo;

import com.iecube.community.model.analysis.dto.TaskAverage;
import com.iecube.community.model.analysis.dto.TaskMedian;
import com.iecube.community.model.analysis.dto.TaskTagCount;
import com.iecube.community.util.ListCounter;
import lombok.Data;

import java.util.List;

@Data
public class CaseHistoryData {
    Integer numberOfParticipant;
    Integer numberOfCompleter;
    double historyAverageGrade;
    List<TaskAverage> TaskAverages;
    List<TaskMedian> TaskMedians;
//    List<ProjectTaskStudentsTags> projectTaskStudentsTagsList;
    List<TaskTagCount> taskTagCountList;
    List<ListCounter.Occurrence> tagsCount;

}
