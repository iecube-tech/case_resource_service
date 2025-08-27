package com.iecube.community.model.EMDV4Project.project.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.EMDV4Project.project.service.EMDV4ProjectService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emdv4/project/")
public class EMDV4ProjectController extends BaseController {

    @Autowired
    private EMDV4ProjectService emdv4ProjectService;

    @PostMapping("/publish")
    public JsonResult<Integer> newProject(@RequestBody EMDV4ProjectQo emdv4ProjectQo){
        int res = emdv4ProjectService.addProject(emdv4ProjectQo, currentUserId());
        return new JsonResult<>(OK, res);
    }

}
