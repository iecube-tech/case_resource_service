package com.iecube.community.model.task.mapper;

import com.iecube.community.model.task.entity.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {

    Integer addProjectTask(Task task);

    Integer addStudentTask(ProjectStudentTask PSK);

    List<StudentTaskVo> findTaskByProjectStudent(Integer projectId, Integer studentId);

    List<StudentTaskDetailVo> findStudentTaskByProjectId(Integer projectId, Integer studentId);

    StudentTaskDetailVo findStudentTaskByPSTId(Integer pstId);

    Integer TeacherModifyProjectStudentTask(ProjectStudentTaskQo projectStudentTaskQo);

    String findSuggestionByTagName(String name);

    List<TaskVo> findByProjectId(Integer projectId);
//    String

    Integer updatePSTStatus(Integer id, Integer status);

    Integer updatePSTContent(Integer id, String content);

    Integer updatePSTResubmit(Integer id, Integer resubmit);
    Integer updatePSTEvaluate(Integer id, String evaluate);
    Integer updatePSTGrade(Integer id, Double grade, Double reportGrade);
    Task findTaskByPSTId(Integer pstId);
    List<TaskVo> getProjectTasks(Integer projectId);
}
