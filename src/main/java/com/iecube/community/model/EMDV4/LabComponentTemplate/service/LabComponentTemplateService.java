package com.iecube.community.model.EMDV4.LabComponentTemplate.service;

import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4.LabComponent.vo.LabComponentVo;

import java.util.List;

public interface LabComponentTemplateService {

    List<LabComponentVo> getBLCTemplate(Long labId);

    List<LabComponentVo> getBLCTemplateByType(Long labId, String type);

    List<LabComponentVo> createBLCTemplate(Long labId, LabComponent labComponent);

    List<LabComponentVo> deleteBLCTemplate(Long labId, Long componentId);

    LabComponentVo updateBLCTemplate(Long labId, LabComponent labComponent);
}
