package com.iecube.community.model.elaborate_md.lab_proc.mapper;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabProcMapper {
    int createLabProc(LabProc labProc);

    int updateLabProc(LabProc newLabProc);

    int batchUpdateSort(List<LabProc> list);

    int deleteLabProc(long id);

    LabProc getLabProcById(long id);

    List<LabProc> getLabProcByCourse(long courseId);
}
