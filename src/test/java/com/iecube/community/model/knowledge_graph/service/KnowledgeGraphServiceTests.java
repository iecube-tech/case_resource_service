package com.iecube.community.model.knowledge_graph.service;

import com.iecube.community.model.knowledge_graph.dto.ItemStyle;
import com.iecube.community.model.knowledge_graph.dto.Label;
import com.iecube.community.model.knowledge_graph.entity.Node;
import com.iecube.community.model.knowledge_graph.mapper.NodeMapper;
import com.iecube.community.model.knowledge_graph.qo.NodeQo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class KnowledgeGraphServiceTests {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @Autowired
    private NodeMapper nodeMapper;

    @Test
    public void justTest(){
        Node node = nodeMapper.getNodeById(82);
        List<Node> sameLevelList = knowledgeGraphService.getSameLevelNode(node);
        System.out.println(sameLevelList);
    }

    @Test
    public void updateNodeTest(){
        Node node = nodeMapper.getNodeById(82);
        NodeQo nodeQo = new NodeQo();
        nodeQo.setId(node.getId());
        nodeQo.setPId(node.getPId());
        nodeQo.setLevel(node.getLevel());
        nodeQo.setName(node.getName());
        ItemStyle itemStyle = new ItemStyle();
        itemStyle.setColor("#000000");
        nodeQo.setItemStyle(itemStyle);
        Label label = new Label();
        label.setFontSize(10);
        label.setPosition(node.getLabelPosition());
        label.setColor(node.getLabelColor());
        nodeQo.setLabel(label);
        nodeQo.setSymbolSize(node.getSymbolSize());
        nodeQo.setLink(node.getLink());
        knowledgeGraphService.updateNode(nodeQo);
    }
}
