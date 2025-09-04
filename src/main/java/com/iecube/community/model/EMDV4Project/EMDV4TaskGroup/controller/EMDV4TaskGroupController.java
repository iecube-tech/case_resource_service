package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.qo.TaskGroupQo;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service.EMDV4TaskGroupService;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/task/group")
public class EMDV4TaskGroupController extends BaseController {
    @Autowired
    private EMDV4TaskGroupService taskGroupService;

    @GetMapping("/my_group")
    public JsonResult<EMDV4TaskGroup> getEMDV4TaskGroup(Long taskId) {
        return new JsonResult<>(OK, taskGroupService.getTaskStudentGroup(taskId, currentUserId()));
    }

    @PostMapping("/create")
        public JsonResult<EMDV4TaskGroup> createTaskGroup(@RequestBody TaskGroupQo taskGroupQo){
        return new JsonResult<>(OK, taskGroupService.createTaskGroup(taskGroupQo,currentUserId()));
    }

    @PostMapping("/code_fresh")
    public JsonResult<EMDV4TaskGroup> taskGroupFreshCode(Long id){
        return new JsonResult<>(OK, taskGroupService.taskGroupFreshCode(id,currentUserId()));
    }

    @PostMapping("/done")
    public JsonResult<EMDV4TaskGroup> taskGroupDone(Long id){
        return new JsonResult<>(OK, taskGroupService.taskGroupSetDoneStatus(id,currentUserId()));
    }


    @DeleteMapping("/del")
    public JsonResult<EMDV4TaskGroup> deleteTaskGroup(Long id){
        return new JsonResult<>(OK, taskGroupService.deleteTaskGroup(id,currentUserId()));
    }

    @GetMapping("/has_not_joined")
    public JsonResult<List<Student>> getTaskStudentNotJoinGroup(Long taskId){
        return new JsonResult<>(OK, taskGroupService.hasNotJoinedGroupStudent(taskId));
    }

    @PostMapping("/add_stu")
    public JsonResult<EMDV4TaskGroup> addStudentsToTaskGroup(@RequestBody TaskGroupQo taskGroupQo){
        return new JsonResult<>(OK, taskGroupService.addStudentsToTaskGroup(taskGroupQo,currentUserId()));
    }

    @PostMapping("/remove_stu")
    public JsonResult<EMDV4TaskGroup> removeStudentsFromTaskGroup(Long id, Integer studentId){
        return new JsonResult<>(OK, taskGroupService.removeStudentsFromTaskGroup(id,studentId,currentUserId()));
    }

    @PostMapping("/join")
    public JsonResult<EMDV4TaskGroup> joinGroup(Long taskId, String code){
        return new JsonResult<>(OK, taskGroupService.joinGroup(taskId,code,currentUserId()));
    }
}
