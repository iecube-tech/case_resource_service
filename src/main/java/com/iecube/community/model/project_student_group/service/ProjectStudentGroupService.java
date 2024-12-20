package com.iecube.community.model.project_student_group.service;

import com.iecube.community.model.project_student_group.entity.Group;
import com.iecube.community.model.project_student_group.entity.GroupCode;
import com.iecube.community.model.project_student_group.entity.GroupStudent;
import com.iecube.community.model.project_student_group.entity.ProjectStudentsWithGroup;
import com.iecube.community.model.project_student_group.vo.GroupVo;
import com.iecube.community.model.student.entity.StudentDto;

import java.util.List;

public interface ProjectStudentGroupService {
    int addGroup(Group group);
    void updateGroup(Group group);
    GroupVo delGroup(Integer id);
    void GroupAddStudent(GroupStudent groupStudent);
    void GroupRemoveStudent(Integer groupId, Integer studentId);
    void GroupAddCode(GroupCode groupCode);

    GroupVo getGroupVoByGroupId(Integer groupId);

    GroupVo getGroupVoByProjectStudent(Integer projectId, Integer studentId);

    GroupVo studentJoinGroup(String code, Integer projectId, Integer studentId);

    GroupCode refreshGroupCode(Integer groupId);

    GroupVo removeStudentFromGroup(Integer groupId, Integer studentId);

    GroupVo addStudentsToGroup(List<ProjectStudentsWithGroup> studentList, Integer groupId);

    GroupVo updateGroupName(Integer groupId, String groupName);

    List<ProjectStudentsWithGroup> projectStudentsWithGroup(Integer projectId);

}
