package com.iecube.community.model.EMDV4.LabStageBlockComponent.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.entity.LabStageBlockComp;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.service.LabStageBlockCompService;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.vo.LabStageBlockCompVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/bl_stage_block_component/")
public class LabStageBlockCompController extends BaseController {

    @Autowired
    private LabStageBlockCompService labStageBlockCompService;

    // 查询block关联的component
    @GetMapping("/block_components")
    public JsonResult<List<LabStageBlockCompVo>> getBlockComponents(Long blockId){
        return new JsonResult<>(OK, labStageBlockCompService.getBlockComponents(blockId));
    }

    // 给block添加 component
    @PostMapping("/add")
    public JsonResult<List<LabStageBlockCompVo>> blockAddComponent(Long blockId, Long componentId){
        return new JsonResult<>(OK, labStageBlockCompService.blockAddComponent(blockId, componentId));
    }

    // 删除block关联的 component
    @DeleteMapping("/del")
    public JsonResult<List<LabStageBlockCompVo>> blockDelComponent(Long labStageBlockCompId){
        return new JsonResult<>(OK, labStageBlockCompService.blockDelComponent(labStageBlockCompId));
    }

    // 批量更新排序编号
    @PostMapping("/component_order_update")
    public JsonResult<List<LabStageBlockCompVo>> updateOrderBatch(@RequestBody List<LabStageBlockComp> list){
        return new JsonResult<>(OK, labStageBlockCompService.updateOrderBatch(list));
    }
}
