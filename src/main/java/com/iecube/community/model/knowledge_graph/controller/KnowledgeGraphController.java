package com.iecube.community.model.knowledge_graph.controller;

import com.iecube.community.basecontroller.knowledge_graph.KnowledgeGraphBaseController;
import com.iecube.community.model.knowledge_graph.qo.NodeQo;
import com.iecube.community.model.knowledge_graph.service.KnowledgeGraphService;
import com.iecube.community.model.knowledge_graph.vo.NodeVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
public class KnowledgeGraphController extends KnowledgeGraphBaseController {
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @PostMapping("/add_root/{caseId}")
    public JsonResult<List> addRootNode(String name, @PathVariable Integer caseId){
        knowledgeGraphService.addRootNode(caseId, name);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @PostMapping("/add_node/{caseId}")
    public JsonResult<List> addNode(@RequestBody NodeQo nodeQo, @PathVariable Integer caseId){
        knowledgeGraphService.addNode(nodeQo);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @PostMapping("/up_node/{caseId}")
    public JsonResult<List> upNode(@RequestBody NodeQo nodeQo, @PathVariable Integer caseId){
        knowledgeGraphService.updateNode(nodeQo);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @DeleteMapping("/del_node/{caseId}/{nodeId}")
    public JsonResult<List> delNode(@PathVariable Integer caseId, @PathVariable Integer nodeId){
        knowledgeGraphService.delNode(nodeId);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @GetMapping("/mapping/{caseId}")
    public JsonResult<List> getCaseMapping(@PathVariable Integer caseId){
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }
}
