package com.iecube.community.model.project_student_group.controller;

import com.iecube.community.basecontroller.project_student_group.ProjectStudentGroupBaseController;
import com.iecube.community.model.project_student_group.entity.Group;
import com.iecube.community.model.project_student_group.entity.GroupCode;
import com.iecube.community.model.project_student_group.entity.ProjectStudentsWithGroup;
import com.iecube.community.model.project_student_group.service.ProjectStudentGroupService;
import com.iecube.community.model.project_student_group.vo.GroupVo;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/pgroup")
public class ProjectStudentGroupController extends ProjectStudentGroupBaseController {
    @Autowired
    private ProjectStudentGroupService projectStudentGroupService;

    @PostMapping("/n")
    public JsonResult<GroupVo> addNewGroup(@RequestBody Group group){
        Integer studentId = currentUserId();
        group.setCreator(studentId);
        group.setLastModifiedUser(studentId);
        group.setCreateTime(new Date());
        group.setLastModifiedTime(new Date());
        int groupId = projectStudentGroupService.addGroup(group);
        GroupVo groupVo = projectStudentGroupService.getGroupVoByGroupId(groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/join/{projectId}")
    public JsonResult<GroupVo> joinGroup(String code, @PathVariable Integer projectId ){
        Integer studentId = currentUserId();
        GroupVo groupVo = projectStudentGroupService.studentJoinGroup(code, projectId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/group/{projectId}/{studentId}")
    public JsonResult<GroupVo> getProjectStudentGroupVo(@PathVariable Integer projectId, @PathVariable Integer studentId){
        GroupVo groupVo = projectStudentGroupService.getGroupVoByProjectStudent(projectId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/group/code/refresh")
    public JsonResult<GroupCode> refreshGroupCode(Integer groupId){
        GroupCode groupCode = projectStudentGroupService.refreshGroupCode(groupId);
        return new JsonResult<>(OK, groupCode);
    }

    @PostMapping("/remove")
    public JsonResult<GroupVo> removeStudentFromGroup(Integer groupId, Integer studentId){
        GroupVo groupVo = projectStudentGroupService.removeStudentFromGroup(groupId, studentId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/group/add/{groupId}")
    public JsonResult<GroupVo> addStudentsToGroup( @RequestBody List<ProjectStudentsWithGroup> studentList,  @PathVariable Integer groupId){
        GroupVo groupVo = projectStudentGroupService.addStudentsToGroup(studentList, groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/uname")
    public JsonResult<GroupVo> updateGroupName(Integer groupId, String groupName){
        GroupVo groupVo = projectStudentGroupService.updateGroupName(groupId, groupName);
        return new JsonResult<>(OK, groupVo);
    }

    @PostMapping("/del")
    public JsonResult<GroupVo> delGroup(Integer groupId){
        GroupVo groupVo = projectStudentGroupService.delGroup(groupId);
        return new JsonResult<>(OK, groupVo);
    }

    @GetMapping("/students/{projectId}")
    public JsonResult<List> projectStudentsWithGroup(@PathVariable Integer projectId){
        List<ProjectStudentsWithGroup> res = projectStudentGroupService.projectStudentsWithGroup(projectId);
        return new JsonResult<>(OK, res);
    }

}
