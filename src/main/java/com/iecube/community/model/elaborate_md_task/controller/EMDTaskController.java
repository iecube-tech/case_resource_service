package com.iecube.community.model.elaborate_md_task.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md_task.entity.EMDSTSBlock;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.task_e_md_proc.entity.TaskEMdProc;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emd_task")
public class EMDTaskController extends BaseController {

    @Autowired
    private EMDTaskService emdTaskService;

    @GetMapping("/tasks")
    public JsonResult<List<EMDTaskVo>> getEMDTaskVoListByProjectId(Integer projectId) {
        List<EMDTaskVo> res = emdTaskService.getEMDTaskVoList(projectId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/task")
    public JsonResult<EMDTaskDetailVo> getEMDTaskDetailVoByTaskId(Integer taskId) {
        Integer studentId = currentUserId();
        EMDTaskDetailVo res = emdTaskService.getTaskDetailVo(taskId, studentId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/ref")
    public JsonResult<String> getTaskEMDProcByTaskId(Integer taskId) {
        return new JsonResult<>(OK, emdTaskService.getTaskEMDProc(taskId));
    }

    @PostMapping("/up/cell/{taskId}/{cellId}")
    public JsonResult<Void> updateBlockStuData(@RequestBody EMDSTSBlock emdstsBlock, @PathVariable Integer taskId, @PathVariable String cellId) {
        emdTaskService.updateEMDSSTSBlockPayload(emdstsBlock, cellId, taskId, currentUserId());
        return new JsonResult<>(OK);
    }
}
