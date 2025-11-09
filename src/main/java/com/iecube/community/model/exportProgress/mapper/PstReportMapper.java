package com.iecube.community.model.exportProgress.mapper;

import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PstReportMapper {
    List<PstReportDTO> getPSTReportListByProjectId(Integer projectId);

    List<PstReportCommentDto> getSTComListByTaskBookId(String taskBookId);
}
