package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTaskSection;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDStudentTaskSectionMapper {
    int BatchAdd(List<EMDStudentTaskSection> list);

    List<EMDTaskSectionVo> getByST(Integer studentId, Integer taskId);
}
