package com.iecube.community.model.elaborate_md.lab_model.mapper;

import com.iecube.community.model.elaborate_md.lab_model.entity.LabModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabModelMapper {
    int createLabModel(LabModel labModel);

    int updateLabModel(LabModel labModel);

    int batchUpdateSort(List<LabModel> list);

    int deleteLabModel(LabModel labModel);

    LabModel getLabModelById(long id);

    List<LabModel> getLabModelsByLabId(long labProcId);
}
