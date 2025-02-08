package com.iecube.community.model.elaborate_md.lab_proc.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.lab_proc.qo.LabProcQo;
import com.iecube.community.model.elaborate_md.lab_proc.service.LabProcService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elaborate/md/lab")
public class LabProcController extends BaseController {

    @Autowired
    private LabProcService labProcService;

    @PostMapping("/create")
    public JsonResult<List<LabProc>> createLabProc(@RequestBody LabProcQo labProcQo) {
        List<LabProc> res =  labProcService.createLabProc(labProcQo);
        return new JsonResult<>(OK, res);
    }

    @DeleteMapping("/del")
    public JsonResult<List<LabProc>> delLabProc(long labProcId) {
        List<LabProc> res = labProcService.deleteLabProc(labProcId);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/up")
    public JsonResult<LabProc> upLabProc(@RequestBody LabProc labProc) {
        LabProc res = labProcService.updateLabProc(labProc);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/sorts")
    public JsonResult<List<LabProc>> sortLabProc(@RequestBody List<LabProc> labProcList) {
        List<LabProc> res = labProcService.batchUpdateSort(labProcList);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/{courseId}")
    public JsonResult<List<LabProc>> getLabProc(@PathVariable long courseId) {
        List<LabProc> res = labProcService.getByCourse(courseId);
        return new JsonResult<>(OK, res);
    }
}
