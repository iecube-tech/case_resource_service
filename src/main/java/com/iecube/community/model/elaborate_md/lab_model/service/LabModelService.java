package com.iecube.community.model.elaborate_md.lab_model.service;

import com.iecube.community.model.elaborate_md.lab_model.entity.LabModel;
import com.iecube.community.model.elaborate_md.lab_model.qo.LabModelQo;
import com.iecube.community.model.elaborate_md.lab_model.vo.LabModelVo;

import java.util.List;

public interface LabModelService {

    List<LabModel> getByLabProc(Long labProcId);

    List<LabModel> createLabModel(LabModelQo labModelQo);

    List<LabModel> batchUpdateSort(List<LabModel> labModelList);

    List<LabModel> updateLabModel(LabModelQo labModelQo);

    List<LabModel> deleteLabModel(LabModelQo labModelQo);

    List<LabModelVo> getLabModelVoList(Long labProcId);
}
