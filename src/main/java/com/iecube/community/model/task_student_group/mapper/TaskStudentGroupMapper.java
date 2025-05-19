package com.iecube.community.model.task_student_group.mapper;

import com.iecube.community.model.task_student_group.entity.Group;
import com.iecube.community.model.task_student_group.entity.GroupCode;
import com.iecube.community.model.task_student_group.entity.GroupStudent;
import com.iecube.community.model.task_student_group.entity.TaskStudentsWithGroup;
import com.iecube.community.model.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskStudentGroupMapper {
    Integer addGroup(Group group);
    Integer updateGroup(Group group);
    Integer delGroup(Integer id);
    Integer GroupAddStudent(GroupStudent groupStudent);
    Integer GroupRemoveStudent(Integer groupId, Integer studentId);
    Integer GroupAddCode(GroupCode groupCode);

    Integer updateGroupSubmitted(Integer groupId, Integer submitted, Integer userId);

    Group getGroupById(Integer id);

    Integer delGroupCode(Integer groupId);

    Group getGroupByTaskStudent(Integer taskId, Integer studentId);

    List<GroupCode> getGroupCodeByCode(String code);
    List<GroupCode> getGroupCodeByGroupId(Integer groupId);
    List<GroupStudent> getStudentsByGroupId(Integer groupId);

    List<GroupStudent> getGroupStudentByStudentId(Integer studentId, Integer taskId);

    List<Student> getStudentByGroup(Integer groupId);

    List<Integer> getPstListByGroupAndTaskId(Integer groupId, Integer taskNum);

    List<TaskStudentsWithGroup> getTaskStudentsWithGroup(Integer taskId);
}
