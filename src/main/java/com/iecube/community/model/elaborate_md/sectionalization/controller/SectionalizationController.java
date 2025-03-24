package com.iecube.community.model.elaborate_md.sectionalization.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.qo.SectionalizationQo;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import com.iecube.community.model.elaborate_md.sectionalization.vo.SectionVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elaborate/md/section")
public class SectionalizationController extends BaseController {

    @Autowired
    private SectionalizationService sectionalizationService;

    @PostMapping("/create")
    public JsonResult<List<Sectionalization>> createSection(@RequestBody SectionalizationQo sectionalizationQo){
        sectionalizationService.createSectionalization(sectionalizationQo);
        List<Sectionalization> res = sectionalizationService.getSectionalizationByLabModeId(sectionalizationQo.getLabModelId());
        return new JsonResult<>(OK, res);
    }

    @DeleteMapping("/del")
    public JsonResult<List<Sectionalization>> delSection(@RequestBody SectionalizationQo sectionalizationQo){
        sectionalizationService.deleteSectionalization(sectionalizationQo);
        List<Sectionalization> res = sectionalizationService.getSectionalizationByLabModeId(sectionalizationQo.getLabModelId());
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/sort")
    public JsonResult<List<Sectionalization>> supSectionSort(@RequestBody List<Sectionalization> list){
        sectionalizationService.updateSectionalizationSort(list);
        List<Sectionalization> res = sectionalizationService.getSectionalizationByLabModeId(list.get(0).getParentId());
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/{labModelId}")
    public JsonResult<List<Sectionalization>> getListByLabProc(@PathVariable long labModelId){
        return new JsonResult<>(OK, sectionalizationService.getSectionalizationByLabModeId(labModelId));
    }

    @GetMapping("/vo/list")
    public JsonResult<List<SectionVo>> getSectionVoListByLab(Long labModelId){
        return new JsonResult<>(OK, sectionalizationService.getSectionVoByLabModelId(labModelId));
    }
}
