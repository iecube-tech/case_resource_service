package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4GroupStudent;
import com.iecube.community.model.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4TaskGroupStudentMapper {
    int insert(EMDV4GroupStudent emdv4GroupStudent);

    int batchInsert(List<EMDV4GroupStudent> list);

    List<Student> getStudentsByGroupId(Long groupId);

    /**
     * 查询emdv4的学生列表
     * @param taskId emdv4TaskId
     * @return `Student` 列表
     */
    List<Student> getStudentsByEMDV4TaskId(Long taskId);

    /**
     * 查询emdvTask中加入小组的学生列表
     * @param taskId emdv4TaskId
     * @return `Student` 列表
     */
    List<Student> getStudentJoinedTaskGroup(Long taskId);

    EMDV4GroupStudent getByGroupAndStudentId(Long groupId, Integer studentId);

    int deleteById(Long id);


}
