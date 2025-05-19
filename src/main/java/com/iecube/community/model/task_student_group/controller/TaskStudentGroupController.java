package com.iecube.community.model.task_student_group.controller;

import com.iecube.community.basecontroller.project_student_group.ProjectStudentGroupBaseController;
import com.iecube.community.model.task_student_group.entity.Group;
import com.iecube.community.model.task_student_group.entity.GroupCode;
import com.iecube.community.model.task_student_group.entity.TaskStudentsWithGroup;
import com.iecube.community.model.task_student_group.service.TaskStudentGroupService;
import com.iecube.community.model.task_student_group.vo.GroupVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/pgroup")
public class TaskStudentGroupController extends ProjectStudentGroupBaseController {
    @Autowired
    private TaskStudentGroupService taskStudentGroupService;

    /**
     * 分组由Project分组 改为 Task分组
     * */
    @PostMapping("/n")
    public JsonResult<GroupVo> addNewGroup(@RequestBody Group group){
        Integer studentId = currentUserId();
        group.setCreator(studentId);
        group.setLastModifiedUser(studentId);
        group.setCreateTime(new Date());
        group.setLastModifiedTime(new Date());
        int groupId = taskStudentGroupService.addGroup(group);
        GroupVo groupVo = taskStudentGroupService.getGroupVoByGroupId(groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/join/{taskId}")
    public JsonResult<GroupVo> joinGroup(String code, @PathVariable Integer taskId ){
        Integer studentId = currentUserId();
        GroupVo groupVo = taskStudentGroupService.studentJoinGroup(code, taskId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/group/{taskId}/{studentId}")
    public JsonResult<GroupVo> getTaskStudentGroupVo(@PathVariable Integer taskId, @PathVariable Integer studentId){
        GroupVo groupVo = taskStudentGroupService.getGroupVoByTaskStudent(taskId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/group/code/refresh")
    public JsonResult<GroupCode> refreshGroupCode(Integer groupId){
        GroupCode groupCode = taskStudentGroupService.refreshGroupCode(groupId);
        return new JsonResult<>(OK, groupCode);
    }

    @PostMapping("/remove")
    public JsonResult<GroupVo> removeStudentFromGroup(Integer groupId, Integer studentId){
        GroupVo groupVo = taskStudentGroupService.removeStudentFromGroup(groupId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/group/add/{groupId}")
    public JsonResult<GroupVo> addStudentsToGroup(@RequestBody List<TaskStudentsWithGroup> studentList, @PathVariable Integer groupId){
        GroupVo groupVo = taskStudentGroupService.addStudentsToGroup(studentList, groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/uname")
    public JsonResult<GroupVo> updateGroupName(Integer groupId, String groupName){
        GroupVo groupVo = taskStudentGroupService.updateGroupName(groupId, groupName);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/del")
    public JsonResult<GroupVo> delGroup(Integer groupId){
        GroupVo groupVo = taskStudentGroupService.delGroup(groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/students/{projectId}/{taskId}")
    public JsonResult<List> taskStudentsWithGroup(@PathVariable Integer projectId, @PathVariable Integer taskId){
        List<TaskStudentsWithGroup> res = taskStudentGroupService.taskStudentsWithGroup(projectId,taskId);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/submitted/{groupId}")
    public JsonResult<GroupVo> updateGroupSubmitted(@PathVariable Integer groupId, Integer submitted){
        String type = currentUserType();
        Integer userId = currentUserId();
        GroupVo groupVo = taskStudentGroupService.updateGroupSubmitted(groupId,submitted, type, userId);
        return new JsonResult<>(OK, groupVo);
    }
}
