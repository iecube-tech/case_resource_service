package com.iecube.community.model.elaborate_md_task.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
