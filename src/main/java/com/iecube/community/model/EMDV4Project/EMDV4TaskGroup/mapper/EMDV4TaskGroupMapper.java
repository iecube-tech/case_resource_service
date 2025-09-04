package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EMDV4TaskGroupMapper {
    int insert(EMDV4TaskGroup record);

    EMDV4TaskGroup getGroupByTaskIdAndStuId(Long taskId, Integer studentId);

    EMDV4TaskGroup getById(Long id);

    int updateCode(Long id, String code);

    int updateStatus(Long id, Integer status);

    int deleteById(Long id);

    EMDV4TaskGroup getByTaskAndCode(Long taskId, String code);
}
