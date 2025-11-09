package com.iecube.community.model.exportProgress.mapper;

import com.iecube.community.model.exportProgress.entity.ExportProgressChild;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExportProgressChildMapper {
    void insert(ExportProgressChild exportProgressChild);
}
