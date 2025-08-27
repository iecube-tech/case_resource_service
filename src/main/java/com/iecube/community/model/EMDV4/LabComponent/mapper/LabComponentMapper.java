package com.iecube.community.model.EMDV4.LabComponent.mapper;

import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabComponentMapper {
    int insert(LabComponent record);

    LabComponent getById(Long id);

    int update(LabComponent record);

    int deleteById(Long id);

    List<LabComponent> getByBlockId(Long blockId);
}
