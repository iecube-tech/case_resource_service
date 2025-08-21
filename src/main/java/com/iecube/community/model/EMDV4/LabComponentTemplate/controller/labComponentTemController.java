package com.iecube.community.model.EMDV4.LabComponentTemplate.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4.LabComponent.vo.LabComponentVo;
import com.iecube.community.model.EMDV4.LabComponentTemplate.service.LabComponentTemplateService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/lab_component_temp")
public class labComponentTemController extends BaseController {

    @Autowired
    private LabComponentTemplateService labComponentTemplateService;

    @GetMapping("/lab_com_temp")
    public JsonResult<List<LabComponentVo>> getByLabId(Long labId){
        return new JsonResult<>(OK, labComponentTemplateService.getBLCTemplate(labId));
    }

    @GetMapping("/type_of_lab_com_temp")
    public JsonResult<List<LabComponentVo>> getByLabAndType(Long labId, String type){
        return new JsonResult<>(OK, labComponentTemplateService.getBLCTemplateByType(labId,type));
    }

    @PostMapping("/{labId}/create")
    public JsonResult<List<LabComponentVo>> createLabComponentTemp(@PathVariable Long labId, @RequestBody LabComponent labComponent){
        return new JsonResult<>(OK, labComponentTemplateService.createBLCTemplate(labId, labComponent));
    }

    @DeleteMapping("/del")
    public JsonResult<List<LabComponentVo>> delLabComponentTemp(Long labId, Long componentId){
        return new JsonResult<>(OK, labComponentTemplateService.deleteBLCTemplate(labId, componentId));
    }

    @PostMapping("/{labId}/update")
    public JsonResult<LabComponentVo> updateLabComponent(@PathVariable Long labId, @RequestBody LabComponent labComponent){
        return new JsonResult<>(OK, labComponentTemplateService.updateBLCTemplate(labId, labComponent));
    }
}
