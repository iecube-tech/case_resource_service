package com.iecube.community.model.knowledge_graph.controller;

import com.iecube.community.basecontroller.knowledge_graph.KnowledgeGraphBaseController;
import com.iecube.community.model.knowledge_graph.entity.CaseNode;
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

    // 弃用
    @PostMapping("/add_root/{caseId}")
    public JsonResult<List> addRootNode(String name, @PathVariable Integer caseId){
        knowledgeGraphService.addRootNode(caseId, name);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @PostMapping("/add_node/{rootId}")
    public JsonResult<List> addNode(@RequestBody NodeQo nodeQo, @PathVariable Integer rootId){
        knowledgeGraphService.addNode(nodeQo);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByRootId(rootId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @PostMapping("/up_node/{rootId}")
    public JsonResult<List> upNode(@RequestBody NodeQo nodeQo, @PathVariable Integer rootId){
        knowledgeGraphService.updateNode(nodeQo);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByRootId(rootId);
        return new JsonResult<>(OK, nodeVoList);
    }

    @DeleteMapping("/del_node/{rootId}/{nodeId}")
    public JsonResult<List> delNode(@PathVariable Integer rootId, @PathVariable Integer nodeId){
        knowledgeGraphService.delNode(nodeId);
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByRootId(rootId);
        return new JsonResult<>(OK, nodeVoList);
    }

    // 弃用
    @GetMapping("/mapping/{caseId}")
    public JsonResult<List> getCaseMapping(@PathVariable Integer caseId){
        List<NodeVo> nodeVoList = knowledgeGraphService.getNodeVoByCourse(caseId);
        return new JsonResult<>(OK, nodeVoList);
    }

    // root 节点列表
    @GetMapping("/list")
    public JsonResult<List> getMapList(){
        return new JsonResult<>(OK, knowledgeGraphService.getAllRoteNodes());
    }

    // 添加root节点
    @PostMapping("/root/add")
    public JsonResult<List> addRoot(String name){
        return new JsonResult<>(OK, knowledgeGraphService.addRootNodeOnly(name));
    }

    @DeleteMapping("/root/del/{rootId}")
    public JsonResult<List> delRootNode(@PathVariable Integer rootId){
        return new JsonResult<>(OK, knowledgeGraphService.delByRootId(rootId));
    }

    // 根据 root 节点获取 mapping
    @GetMapping("/mapping/root/{rootId}")
    public JsonResult<List> getMappingByRootId(@PathVariable Integer rootId){
        return new JsonResult<>(OK, knowledgeGraphService.getNodeVoByRootId(rootId));
    }

    //管理案例和node
    @PostMapping("/mapping/case")
    public JsonResult<Void> connectMapCase(Integer caseId, Integer nodeId){
        knowledgeGraphService.connectCaseNode(caseId, nodeId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/{caseId}")
    public JsonResult<CaseNode> getCaseNodeByCaseId(@PathVariable Integer caseId){
        return new JsonResult<>(OK, knowledgeGraphService.getCaseNodeByCaseId(caseId));
    }
}
