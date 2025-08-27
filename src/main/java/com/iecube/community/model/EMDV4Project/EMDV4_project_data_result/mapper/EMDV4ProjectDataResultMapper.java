package com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.entity.EMDV4ProjectDataResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EMDV4ProjectDataResultMapper {
    int insert(EMDV4ProjectDataResult record);
}
