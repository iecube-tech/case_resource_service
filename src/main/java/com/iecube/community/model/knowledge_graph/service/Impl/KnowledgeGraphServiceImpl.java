package com.iecube.community.model.knowledge_graph.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.knowledge_graph.dto.ItemStyle;
import com.iecube.community.model.knowledge_graph.dto.Label;
import com.iecube.community.model.knowledge_graph.entity.CaseNode;
import com.iecube.community.model.knowledge_graph.entity.Node;
import com.iecube.community.model.knowledge_graph.mapper.CaseNodeMapper;
import com.iecube.community.model.knowledge_graph.mapper.NodeMapper;
import com.iecube.community.model.knowledge_graph.qo.NodeQo;
import com.iecube.community.model.knowledge_graph.service.KnowledgeGraphService;
import com.iecube.community.model.knowledge_graph.vo.NodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {
    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private CaseNodeMapper caseNodeMapper;

    public List<Node> allNodeList = new ArrayList<>();

    @Override
    public void addRootNode(Integer courseId, String name) {
        CaseNode oldCaseNode = caseNodeMapper.getByCaseId(courseId);
        if(oldCaseNode!=null){
            return;
        }
        Node node = new Node();
        node.setName(name);
        node.setLevel(1);
        node.setItemColor("#343838");
        node.setLabelColor("inherit");
        node.setLabelPosition("left");
        node.setLabelFontSize(26);
        node.setSymbolSize(30);
        node.setLink("");
        Integer res = nodeMapper.insertNode(node);
        if(res!= 1){
            throw new InsertException("新增map数据异常");
        }
        CaseNode caseNode = new CaseNode();
        caseNode.setCaseId(courseId);
        caseNode.setNodeId(node.getId());
        Integer res1 = caseNodeMapper.insertCaseNode(caseNode);
        if(res1!=1){
            throw new InsertException("新增map_case_node数据异常");
        }
    }

    @Override
    public void addNode(NodeQo nodeQo) {
        if(nodeQo.getId()!=null){
            this.updateNode(nodeQo);
            return;
        }
        if(nodeQo.getPId() == null){
            throw new InsertException("添加错误，未找到父节点");
        }
        Node node = this.nodeQoToNode(nodeQo);
        Integer res = nodeMapper.insertNode(node);
        if(res!= 1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public void updateNode(NodeQo nodeQo) {
        if(nodeQo.getId() == null){
            this.addNode(nodeQo);
            return;
        }
        Node node = this.nodeQoToNode(nodeQo);
        Integer res = nodeMapper.updateNode(node);
        if(res!= 1){
            throw new UpdateException("更新数据异常");
        }
        this.updateSameLevelNode(node);
    }

    public void updateSameLevelNode(Node node){
        List<Node> sameLevelNodeList = this.getSameLevelNode(node);
        for (Node nd : sameLevelNodeList){
            nd.setSymbolSize(node.getSymbolSize());
            nd.setLabelPosition(node.getLabelPosition());
            nd.setLabelFontSize(node.getLabelFontSize());
            nd.setLabelColor(node.getLabelColor());
            nd.setItemColor(node.getItemColor());
        }
        nodeMapper.batchUpdateMapNode(sameLevelNodeList);
//        System.out.println(sameLevelNodeList.size());
//        System.out.println(res);
    }

    @Override
    public List<Node> getSameLevelNode(Node node){
        Node rootNode = this.getRootNode(node);
        List<Node> nodeTree = this.getNodeTree(rootNode.getId());
        List<Node> allTreeNode = this.getAllNodeInTree(nodeTree);
        List<Node> sameLevelNode = new ArrayList<>();
        for(Node nd : allTreeNode){
            if(nd.getLevel().equals(node.getLevel())){
                sameLevelNode.add(nd);
            }
        }
        return sameLevelNode;
    }

    @Override
    public List<Node> getAllRoteNodes() {
        return nodeMapper.getAllRootNode();
    }

    @Override
    public List<Node> addRootNodeOnly(String name) {
        Node node = new Node();
        node.setName(name);
        node.setLevel(1);
        node.setItemColor("#343838");
        node.setLabelColor("inherit");
        node.setLabelPosition("left");
        node.setLabelFontSize(26);
        node.setSymbolSize(30);
        node.setLink("");
        Integer res = nodeMapper.insertNode(node);
        if(res!= 1){
            throw new InsertException("新增map数据异常");
        }
        return this.getAllRoteNodes();
    }

    @Override
    public List<Node> delByRootId(Integer rootId) {
        CaseNode caseNodeList = caseNodeMapper.getByNodeId(rootId);
        if(caseNodeList!=null){
            throw new DeleteException("理实映射已关联课程，无法删除");
        }
        // todo 删除节点
        return Collections.emptyList();
    }

    @Override
    public List<NodeVo> getNodeVoByRootId(Integer rootId) {
        return this.getTree(rootId);
    }

    @Override
    public void connectCaseNode(Integer caseId, Integer nodeId) {
        CaseNode caseNode = new CaseNode();
        caseNode.setCaseId(caseId);
        caseNode.setNodeId(nodeId);
        caseNodeMapper.deleteByCaseId(caseId);
        int res = caseNodeMapper.insertCaseNode(caseNode);
        if(res!= 1){
            throw  new InsertException("更新数据异常");
        }
    }

    @Override
    public CaseNode getCaseNodeByCaseId(Integer caseId) {
        return caseNodeMapper.getByCaseId(caseId);
    }

    public Node getRootNode(Node childNode){
        Node parentNode = nodeMapper.getNodeById(childNode.getPId());
        if(parentNode.getPId()!=null){
            parentNode = getRootNode(parentNode);
        }
        return parentNode;
    }

    @Override
    public void delNode(Integer id) {
        Node node = nodeMapper.getNodeById(id);
        if(node.getLevel().equals(1)){
            throw new DeleteException("不允许删除根节点");
        }
        Integer res = nodeMapper.delNode(id);
        if(res != 1) {
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public List<NodeVo> getNodeVoByCourse(Integer courseId){
        CaseNode caseNode = caseNodeMapper.getByCaseId(courseId);
        if(caseNode == null){
            return new ArrayList<>();
        }
        Integer rootId = caseNode.getNodeId();
        List<NodeVo> nodeVoList = this.getTree(rootId);
        return nodeVoList;
    }

    private Node nodeQoToNode(NodeQo nodeQo){
        Node node = new Node();
        node.setId(nodeQo.getId());
        node.setPId(nodeQo.getPId());
        node.setLevel(nodeQo.getLevel());
        node.setName(nodeQo.getName());
        node.setItemColor(nodeQo.getItemStyle().getColor());
        node.setLabelColor(nodeQo.getLabel().getColor());
        node.setLabelPosition(nodeQo.getLabel().getPosition());
        node.setLabelFontSize(nodeQo.getLabel().getFontSize());
        node.setSymbolSize(nodeQo.getSymbolSize());
        node.setLink(nodeQo.getLink());
        return node;
    }

    private NodeVo nodeToNodeVo(Node node){
        NodeVo nodeVo = new NodeVo();
        nodeVo.setId(node.getId());
        nodeVo.setPId(node.getPId());
        nodeVo.setLevel(node.getLevel());
        nodeVo.setName(node.getName());
        ItemStyle itemStyle = new ItemStyle();
        itemStyle.setColor(node.getItemColor());
        nodeVo.setItemStyle(itemStyle);
        Label label = new Label();
        label.setColor(node.getLabelColor());
        label.setPosition(node.getLabelPosition());
        label.setFontSize(node.getLabelFontSize());
        nodeVo.setLabel(label);
        nodeVo.setSymbolSize(node.getSymbolSize());
        nodeVo.setLink(node.getLink());
        nodeVo.setChildren(new ArrayList<>());
        return nodeVo;
    }

    /**
     * 根据根节点构建树
     * @param rootId 根节点
     * @return List<NodeVo>
     */
    public List<NodeVo> getTree(Integer rootId){
        List<NodeVo> nodeVoList = new ArrayList<>();
        // 根节点的信息
        Node node = nodeMapper.getNodeById(rootId);
        NodeVo nodeVo = this.nodeToNodeVo(node);
        // 填充根节点的子节点
        nodeVo = this.getChildTree(nodeVo);

        nodeVoList.add(nodeVo);
        return nodeVoList;
    }

    /**
     * 递归构建子树
     * @param pNodeVo
     * @return NodeVo
     */
    public NodeVo getChildTree(NodeVo pNodeVo){
        allNodeList = nodeMapper.allNode();
        List<NodeVo> childTree = new ArrayList<>();
        // 在所有的节点中判断其pId与当前的pNode的id是不是相同 相同就是他的子节点
        for(Node node : allNodeList){
            if(node.getPId()!=null){
                if(node.getPId().equals(pNodeVo.getId())){
                    NodeVo nodeVo = this.nodeToNodeVo(node);
                    // 递归实现
                    childTree.add(getChildTree(nodeVo));
                }
            }
        }
        pNodeVo.setChildren(childTree);
        return pNodeVo;
    }


    public List<Node> getNodeTree(Integer rootId){
        List<Node> nodeList = new ArrayList<>();
        Node node = nodeMapper.getNodeById(rootId);
        node = this.getNodeChildTree(node);
        nodeList.add(node);
        return nodeList;
    }

    public Node getNodeChildTree(Node pNode){
        allNodeList = nodeMapper.allNode();
        List<Node> childTree = new ArrayList<>();
        for(Node node : allNodeList){
            if(node.getPId()!=null){
                if(node.getPId().equals(pNode.getId())){
                    childTree.add(getNodeChildTree(node));
                }
            }
        }
        pNode.setChildren(childTree);
        return pNode;
    }

    /**
     * 获取树中的所有节点
     * @param nodeList
     * @return
     */
    public List<Node> getAllNodeInTree(List<Node> nodeList){
        List<Node> allTreeNode = new ArrayList<>();
        for(Node node:nodeList){
            Node newNode = new Node();
            newNode.setId(node.getId());
            newNode.setPId(node.getPId());
            newNode.setName(node.getName());
            newNode.setLink(node.getLink());
            newNode.setLevel(node.getLevel());
            newNode.setLabelColor(node.getLabelColor());
            newNode.setLabelPosition(node.getLabelPosition());
            newNode.setLabelFontSize(node.getLabelFontSize());
            newNode.setItemColor(node.getItemColor());
            newNode.setSymbolSize(node.getSymbolSize());
            allTreeNode.add(newNode);
            if(node.getChildren().size()>0){
                allTreeNode.addAll(getAllNodeInTree(node.getChildren()));
            }
        }
        return allTreeNode;
    }
}
