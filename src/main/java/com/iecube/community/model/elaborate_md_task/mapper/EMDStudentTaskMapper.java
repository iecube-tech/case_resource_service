package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.dto.EMDStuTaskDetailDto;
import com.iecube.community.model.elaborate_md_task.dto.PSTDto;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import com.iecube.community.model.task.entity.StudentTaskVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface EMDStudentTaskMapper {
    int BatchAdd(List<EMDStudentTask> list);

    List<StudentTaskVo> emdCourseStudentTaskVo(Integer projectId);

    int updateStatus(Integer studentId, Integer taskId, Integer status);

    void updateStartTime(Integer studentId, Integer taskId, Date startTime);

    void updateEndTime(Integer studentId, Integer taskId, Date endTime);

    EMDStudentTask getByStudentIdTaskId(Integer studentId, Integer taskId);

    EMDStudentTask getById(Long pstId);

    void upPSTGrade(Long pstId, double grade);

    /**
     * 根据一个pstId 获取该学生的该project下的所有pst
     * @param pstId pstId
     * @return List<PSTDto>
     */
    List<PSTDto> studentPSTList(Long pstId);

    /**
     * 根据pstId 去更改对应的projectStudent的成绩
     * @param pstId pstId
     * @param grade projectStudent 的成绩
     */
    void upPSGrade(Long pstId, double grade);
}
