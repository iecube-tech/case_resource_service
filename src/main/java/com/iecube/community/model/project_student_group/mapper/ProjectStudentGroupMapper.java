package com.iecube.community.model.project_student_group.mapper;

import com.iecube.community.model.project_student_group.entity.Group;
import com.iecube.community.model.project_student_group.entity.GroupCode;
import com.iecube.community.model.project_student_group.entity.GroupStudent;
import com.iecube.community.model.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectStudentGroupMapper {
    Integer addGroup(Group group);
    Integer updateGroup(Group group);
    Integer delGroup(Integer id);
    Integer GroupAddStudent(GroupStudent groupStudent);
    Integer GroupRemoveStudent(Integer groupId, Integer studentId);
    Integer GroupAddCode(GroupCode groupCode);

    Integer updateGroupSubmitted(Integer groupId);

    Group getGroupById(Integer id);

    Integer delGroupCode(Integer groupId);

    Group getGroupByProjectStudent(Integer projectId, Integer studentId);

    List<GroupCode> getGroupCodeByCode(String code);
    List<GroupCode> getGroupCodeByGroupId(Integer groupId);
    List<GroupStudent> getStudentsByGroupId(Integer groupId);

    List<GroupStudent> getGroupStudentByStudentId(Integer studentId, Integer projectId);

    List<Student> getStudentByGroup(Integer groupId);
}
