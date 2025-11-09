package com.iecube.community.model.exportProgress.mapper;

import com.iecube.community.model.exportProgress.entity.ExportProgress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExportProgressMapper {

    void insert(ExportProgress exportProgress);

    ExportProgress selectById(String id);

    ExportProgress selectByProject(Integer projectId, String type);

    void updateById(ExportProgress exportProgress);
}
