package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.StuMonitor;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.TaskStepDto;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectMonitorMapper {
    MonitorInfoVo getMonitorInfo(Integer projectId);

    Integer getTotalStuNum(Integer projectId);

    List<TaskStepDto> getAllStuTaskStep(Integer projectId);

    /**
     * 返回的数据包含 【学生所有实验】 的 level=1 level=2的节点</br>
     * 已根据 ps_id, pt_id, block_stage 排序
     * @param projectId projectId
     * @param offset offset 偏移量
     * @param pageSize pageSize 每页数量
     * @return List<StuMonitor>
     */
    List<StuMonitor> getStuMonitorPaging(Integer projectId, Integer offset, Integer pageSize);

    /**
     * 返回数据包含【所有学生所有实验】的level=1 level=2的节点</br>
     * 已根据ps_id, pst_id, pt_id, block_stage, block_level, block_order排序
     * @param projectId projectId
     * @return List
     */
    List<StuMonitor> getStuMonitorAll(Integer projectId);

    /**
     * 返回的数据包含 【分页学生实验】 实验指导书的叶子节点和Level=1 Level=2的节点， 其中level=1,2的值会有重复值</br>
     * 已根据ps_id, block_stage, block_level, block_order 排序
     * @param projectId projectId
     * @param ptId ptId
     * @param offset offset 偏移量
     * @param pageSize pageSize 每页数量
     * @return List<StuMonitor>
     */
    List<StuMonitor> getTaskStuMonitorPaging(Integer projectId, Long ptId, Integer offset, Integer pageSize);

    /**
     * 返回的数据包含 【全部学生实验】 实验指导书的 `叶子节点` 和Level=1 Level=2的节点， 其中level=1,2的值会有重复值</br>
     * 已根据ps_id, block_stage, block_level, block_order 排序
     * @param projectId projectId
     * @param ptId ptId
     * @return List<StuMonitor>
     */
    List<StuMonitor> getTaskStuMonitorAll(Integer projectId, Long ptId);

    /**
     * 返回数据包含学生的所有实验的 level=1 level=2节点及叶子节点</br>
     * 已根据pt_id, block_stage, block_level, block_order排序
     * @param projectId projectId
     * @param psId psId
     * @return List<StuMonitor>
     */
    List<StuMonitor> getStuTaskMonitor(Integer projectId, Long psId);

    /**
     * 返回学号或姓名包含关键字的所有实验的  level=1 level=2 节点
     */
    List<StuMonitor> studentSearch(Integer projectId, String val);

    /**
     * 返回 对应实验 学号或姓名包含关键字的  level=1 level=2 叶子 节点
     */
    List<StuMonitor> taskStudentSearch(Integer projectId, Long ptId, String val);
}
