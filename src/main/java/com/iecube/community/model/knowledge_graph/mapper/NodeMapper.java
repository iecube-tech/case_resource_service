package com.iecube.community.model.knowledge_graph.mapper;

import com.iecube.community.model.knowledge_graph.entity.Node;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NodeMapper {
    Integer insertNode(Node node);

    Integer updateNode(Node node);

    Integer delNode(Integer id);

    Node getNodeById(Integer id);

    List<Node> getNodeByPId(Integer pId);

    List<Node> allNode();

    Integer batchUpdateMapNode(@Param("list")  List<Node> nodeList);

    List<Node> getAllRootNode();
}
