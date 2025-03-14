package com.iecube.community.model.elaborate_md.lab_proc.service;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.lab_proc.qo.LabProcQo;

import java.util.List;

public interface LabProcService {
    List<LabProc> getByCourse(long courseId);

    List<LabProc> createLabProc(LabProcQo labProcQo);

    List<LabProc> batchUpdateSort(List<LabProc> labProcList);

    List<LabProc> updateLabProc(LabProcQo labProcQo);

    List<LabProc> deleteLabProc(LabProcQo labProcQo);

}
