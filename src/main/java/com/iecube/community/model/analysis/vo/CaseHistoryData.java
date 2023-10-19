package com.iecube.community.model.analysis.vo;

import com.iecube.community.model.analysis.dto.TaskAverage;
import com.iecube.community.model.analysis.dto.TaskMedian;
import com.iecube.community.model.analysis.dto.TaskTagCount;
import com.iecube.community.util.ListCounter;
import lombok.Data;

import java.util.List;

@Data
public class CaseHistoryData {
    Integer numberOfParticipant;  //参与人数
    Integer numberOfCompleter;  // 完成人数
    double historyAverageGrade; //案例的平均分数
    List<TaskAverage> TaskAverages; // 任务平均分数
    List<TaskMedian> TaskMedians; // 历史任务的中位数
//    List<ProjectTaskStudentsTags> projectTaskStudentsTagsList;
    List<TaskTagCount> taskTagCountList; // 任务的的tag点统计列表
    List<ListCounter.Occurrence> tagsCount; // tag计数

}
