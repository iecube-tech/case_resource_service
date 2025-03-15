package com.iecube.community.model.elaborate_md.lab_proc.mapper;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProcRef;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabProcRefMapper {

    int insert(LabProcRef record);

    int updateByLabId(LabProcRef record);

    LabProcRef getByLabId(long labId);
}
