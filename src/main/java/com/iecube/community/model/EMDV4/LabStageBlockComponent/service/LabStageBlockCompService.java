package com.iecube.community.model.EMDV4.LabStageBlockComponent.service;


import com.iecube.community.model.EMDV4.LabStageBlockComponent.entity.LabStageBlockComp;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.vo.LabStageBlockCompVo;

import java.util.List;

public interface LabStageBlockCompService {

    List<LabStageBlockCompVo> getBlockComponents(Long blockId);

    List<LabStageBlockCompVo> blockAddComponent(Long blockId, Long componentId);

    List<LabStageBlockCompVo> blockDelComponent(Long labStageBlockCompId);

    List<LabStageBlockCompVo> updateOrderBatch(List<LabStageBlockComp> list);
}
