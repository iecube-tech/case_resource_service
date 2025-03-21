package com.iecube.community.model.knowledge_graph.service;

import com.iecube.community.model.knowledge_graph.entity.CaseNode;
import com.iecube.community.model.knowledge_graph.entity.Node;
import com.iecube.community.model.knowledge_graph.qo.NodeQo;
import com.iecube.community.model.knowledge_graph.vo.NodeVo;

import java.util.List;

public interface KnowledgeGraphService {

    void addRootNode(Integer courseId, String name);

    void addNode(NodeQo nodeQo);

    void updateNode(NodeQo nodeQo);

    void delNode(Integer id);

    List<NodeVo> getNodeVoByCourse(Integer courseId);

    List<Node> getSameLevelNode(Node node);

    List<Node> getAllRoteNodes();

    List<Node> addRootNodeOnly(String name);

    List<Node> delByRootId(Integer rootId);

    List<NodeVo> getNodeVoByRootId(Integer rootId);

    void connectCaseNode(Integer caseId, Integer nodeId);

    CaseNode getCaseNodeByCaseId(Integer caseId);
}
