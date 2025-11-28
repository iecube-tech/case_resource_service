package com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.CompTargetTagDto;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.PSTAIDto;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.PSTDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataSourceMapper {
    List<PSTDto> getProjectPSTWithStage(Integer projectId);

    List<PSTAIDto> getProjectPSTAIDTO(Integer projectId);

    List<CompTargetTagDto> getCompTargetTagDtoByProject(Integer projectId);
}
