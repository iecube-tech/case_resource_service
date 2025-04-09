package com.iecube.community.model.task_student_group.service;

import com.iecube.community.model.task_student_group.entity.Group;
import com.iecube.community.model.task_student_group.entity.GroupCode;
import com.iecube.community.model.task_student_group.entity.GroupStudent;
import com.iecube.community.model.task_student_group.entity.TaskStudentsWithGroup;
import com.iecube.community.model.task_student_group.vo.GroupVo;

import java.util.List;

public interface TaskStudentGroupService {
    int addGroup(Group group);
    void updateGroup(Group group);
    GroupVo delGroup(Integer id);
    void GroupAddStudent(GroupStudent groupStudent);
    void GroupRemoveStudent(Integer groupId, Integer studentId);
    void GroupAddCode(GroupCode groupCode);

    GroupVo getGroupVoByGroupId(Integer groupId);

    GroupVo getGroupVoByTaskStudent(Integer taskId, Integer studentId);

    GroupVo studentJoinGroup(String code, Integer taskId, Integer studentId);

    GroupCode refreshGroupCode(Integer groupId);

    GroupVo removeStudentFromGroup(Integer groupId, Integer studentId);

    GroupVo addStudentsToGroup(List<TaskStudentsWithGroup> studentList, Integer groupId);

    GroupVo updateGroupName(Integer groupId, String groupName);

    GroupVo updateGroupSubmitted(Integer groupId, Integer submitted,String type, Integer userId);

    List<TaskStudentsWithGroup> taskStudentsWithGroup(Integer projectId, Integer taskId);

}
