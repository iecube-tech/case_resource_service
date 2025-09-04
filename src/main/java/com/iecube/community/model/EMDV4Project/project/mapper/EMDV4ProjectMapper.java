package com.iecube.community.model.EMDV4Project.project.mapper;

import com.iecube.community.model.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EMDV4ProjectMapper {
    Project getProjectByEMDV4Task(Long taskId);
}
