package com.iecube.community.model.EMDV4Project.EMDV4Analysis.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.EMDV4AnalysisService;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.jwt.CurrentUser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emdv4/analysis")
public class EMDV4AnalysisController extends BaseController {

    @Autowired
    private EMDV4AnalysisService service;

    @GetMapping("/test")
    public JsonResult<AnalysisProgress> gen() {
        AnalysisProgress res = service.createGenProgress(246);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/gen/{projectId}")
    public JsonResult<AnalysisProgress> genData(@PathVariable Integer projectId) {
        AnalysisProgress res = service.createGenProgress(projectId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/{projectId}/{type}")
    public JsonResult<JsonNode> getData(@PathVariable Integer projectId, @PathVariable  String type) {
        JsonNode res = service.getData(projectId, type);
        return new JsonResult<>(OK,res);
    }

    @GetMapping("/task/{projectId}/{type}/{ptId}")
    public JsonResult<JsonNode> getTaskData(@PathVariable Integer projectId, @PathVariable  String type, @PathVariable Long ptId) {
        JsonNode res = service.getTaskData(projectId, type, ptId);
        return new JsonResult<>(OK,res);
    }

    @GetMapping("/stu/{projectId}/{type}/{studentId}")
    public JsonResult<JsonNode> getStuData(@PathVariable Integer projectId, @PathVariable  String type, @PathVariable String studentId) {
        JsonNode res = service.getStuData(projectId, type, studentId);
        return new JsonResult<>(OK,res);

    }

    @GetMapping("/pst/{projectId}/{type}/{ptId}/{psId}")
    public JsonResult<JsonNode> getPSTData(@PathVariable Integer projectId, @PathVariable  String type, @PathVariable Long ptId, @PathVariable Long psId) {
        JsonNode res = service.getPSTData(projectId, type, ptId, psId);
        return new JsonResult<>(OK,res);

    }
}
