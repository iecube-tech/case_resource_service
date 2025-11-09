package com.iecube.community.model.exportProgress.mapper;

import com.iecube.community.model.exportProgress.entity.ExportProgress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExportProgressMapper {

    void insert(ExportProgress exportProgress);

    ExportProgress selectById(String id);

    List<ExportProgress> selectByProject(Integer projectId);

    void updateById(ExportProgress exportProgress);

    void delById(String id);
}
