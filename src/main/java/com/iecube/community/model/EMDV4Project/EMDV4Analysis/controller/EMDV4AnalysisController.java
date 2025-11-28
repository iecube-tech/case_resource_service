package com.iecube.community.model.EMDV4Project.EMDV4Analysis.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.EMDV4AnalysisService;
import com.iecube.community.util.JsonResult;
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
}
