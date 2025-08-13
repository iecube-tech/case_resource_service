package com.iecube.community.model.EMDV4.CourseTarget.service.impl;

import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;
import com.iecube.community.model.EMDV4.CourseTarget.mapper.CourseTargetMapper;
import com.iecube.community.model.EMDV4.CourseTarget.service.CourseTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseTargetServiceImpl implements CourseTargetService {

    @Autowired
    private CourseTargetMapper courseTargetMapper;


    /**
     *
     * @param MF 专业方向的id
     * @return 专业方向下的毕业要求，毕业要求指标点，课程目标的树形结构
     */
    @Override
    public List<CourseTarget> getCourseTargetByMF(Long MF) {
        List<CourseTarget> courseTargetList = courseTargetMapper.getAllTargetByMF(MF);
        return buildCourseTargetTrees(courseTargetList);
    }

    /**
     * 将一个没有结构关系的 List<CourseTarget> 转换为树形结构
     * @param courseTargetList CourseTarget列表
     * @return 树形结构
     */
    private List<CourseTarget> buildCourseTargetTrees(List<CourseTarget> courseTargetList) {
        // 2. 转换为包含子节点的节点对象并建立ID到节点的映射
        Map<Long, CourseTarget> nodeMap = new HashMap<>();
        for (CourseTarget field : courseTargetList) {
            nodeMap.put(field.getId(), field);
        }
        // 3. 收集所有根节点（pId为null或0的节点）
        List<CourseTarget> rootNodes = new ArrayList<>();

        // 4. 构建树形结构
        for (CourseTarget field : courseTargetList) {
            CourseTarget currentNode = nodeMap.get(field.getId());
            Long pId = field.getPId();

            // 如果父ID为null或0，视为根节点
            if (pId == null || pId == 0) {
                rootNodes.add(currentNode);
            } else {
                // 找到父节点并添加为子节点
                CourseTarget parentNode = nodeMap.get(pId);
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(currentNode);
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
