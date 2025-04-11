package com.iecube.community.model.elaborate_md_task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md_task.check.Check;
import com.iecube.community.model.elaborate_md_task.check.CheckProcessingService;
import com.iecube.community.model.elaborate_md_task.entity.EMDSTMSBlock;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskModelVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskRefVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/emd_task")
public class EMDTaskController extends BaseController {

    @Autowired
    private EMDTaskService emdTaskService;

    @Autowired
    private CheckProcessingService checkProcessingService;

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
    public JsonResult<EMDTaskRefVo> getTaskEMDProcByTaskId(Integer taskId) {
        return new JsonResult<>(OK, emdTaskService.getTaskEMDProc(taskId));
    }

    @PostMapping("/up/cell/{taskId}/{cellId}")
    public JsonResult<Void> updateBlockStuData(@RequestBody EMDSTMSBlock EMDSTMSBlock, @PathVariable Integer taskId, @PathVariable String cellId) {
        emdTaskService.updateEMDSSTSBlockPayload(EMDSTMSBlock, cellId, taskId, currentUserId());
        return new JsonResult<>(OK);
    }

    @PostMapping("/section/next")
    public JsonResult<Boolean> nextSection(Integer STMSId) {
        Boolean res = emdTaskService.toNextSection(STMSId.longValue());
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/model/update")
    public JsonResult<EMDTaskModelVo> nextModel(Integer modelId, Integer currAskNum, Integer status) {
        EMDTaskModelVo emdTaskModelVo = emdTaskService.upModelStatus(modelId.longValue(),status, currAskNum);
        return new JsonResult<>(OK, emdTaskModelVo);
    }

    @PostMapping("/dlog/upload/{studentId}/{taskId}")
    public JsonResult<Void> uploadEMDLog(MultipartFile file, @PathVariable Integer studentId, @PathVariable Integer taskId) {
        emdTaskService.uploadDeviceLog(studentId, taskId, file);
        return new JsonResult<>(OK);
    }

    @PostMapping("/check")
    public JsonResult<Void> checkEMDTask(@RequestBody Check check) {
        checkProcessingService.addTask(check);
        return new JsonResult<>(OK);
    }
}
