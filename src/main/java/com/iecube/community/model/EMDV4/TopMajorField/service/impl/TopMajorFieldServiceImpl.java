package com.iecube.community.model.EMDV4.TopMajorField.service.impl;

import com.iecube.community.model.EMDV4.TopMajorField.entity.MajorField;
import com.iecube.community.model.EMDV4.TopMajorField.mapper.TopMajorFieldMapper;
import com.iecube.community.model.EMDV4.TopMajorField.service.TopMajorFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopMajorFieldServiceImpl implements TopMajorFieldService {

    @Autowired
    private TopMajorFieldMapper topMajorFieldMapper;

    @Override
    public List<MajorField> getAllMajorField() {
        return buildMultiTrees();
    }

    /**
     * 获取所有MajorField并构建成多棵树
     * @return 多棵树的根节点列表
     */
    public List<MajorField> buildMultiTrees() {
        // 1. 从数据库获取所有MajorField
        List<MajorField> allFields = topMajorFieldMapper.getAll();
        if (allFields == null || allFields.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 转换为包含子节点的节点对象并建立ID到节点的映射
        Map<Long, MajorField> nodeMap = new HashMap<>();
        for (MajorField field : allFields) {
            nodeMap.put(field.getId(), field);
        }

        // 3. 收集所有根节点（pId为null或0的节点）
        List<MajorField> rootNodes = new ArrayList<>();

        // 4. 构建树形结构
        for (MajorField field : allFields) {
            MajorField currentNode = nodeMap.get(field.getId());
            Long pId = field.getPId();

            // 如果父ID为null或0，视为根节点
            if (pId == null || pId == 0) {
                rootNodes.add(currentNode);
            } else {
                // 找到父节点并添加为子节点
                MajorField parentNode = nodeMap.get(pId);
                if (parentNode != null) {
                    if(parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.addChild(currentNode);
                } else {
                    // 处理父节点不存在的情况，可根据实际业务决定是否作为根节点
                    // 这里将其作为根节点处理
                    rootNodes.add(currentNode);
                }
            }
        }
        return rootNodes;
    }
}
