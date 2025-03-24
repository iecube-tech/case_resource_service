package com.iecube.community.model.elaborate_md.lab_model.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md.lab_model.entity.LabModel;
import com.iecube.community.model.elaborate_md.lab_model.qo.LabModelQo;
import com.iecube.community.model.elaborate_md.lab_model.service.LabModelService;
import com.iecube.community.model.elaborate_md.lab_model.vo.LabModelVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elaborate/md/lab_model")
public class LabModelController extends BaseController {
    @Autowired
    private LabModelService labModelService;

    @PostMapping("/create")
    public JsonResult<List<LabModel>> create(@RequestBody LabModelQo labModelQo) {
        List<LabModel> res = labModelService.createLabModel(labModelQo);
        return  new JsonResult<>(OK, res);
    }

    @DeleteMapping("/del")
    public JsonResult<List<LabModel>> deleteLabModel(@RequestBody LabModelQo labModelQo) {
        List<LabModel> res = labModelService.deleteLabModel(labModelQo);
        return  new JsonResult<>(OK, res);
    }

    @PostMapping("/up")
    public JsonResult<List<LabModel>> up(@RequestBody LabModelQo labModelQo) {
        List<LabModel> res = labModelService.updateLabModel(labModelQo);
        return  new JsonResult<>(OK, res);
    }

    @PostMapping("/sort")
    public JsonResult<List<LabModel>> sort(@RequestBody List<LabModel> labModelList) {
        List<LabModel> res = labModelService.batchUpdateSort(labModelList);
        return  new JsonResult<>(OK, res);
    }

    @GetMapping("/{labProcId}")
    public JsonResult<List<LabModel>> getLabModels(@PathVariable long labProcId) {
        List<LabModel> res = labModelService.getByLabProc(labProcId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/list/model_vo")
    public JsonResult<List<LabModelVo>> getLabModelVoList(long labProcId) {
        List<LabModelVo> res = labModelService.getLabModelVoList(labProcId);
        return new JsonResult<>(OK, res);
    }


}
