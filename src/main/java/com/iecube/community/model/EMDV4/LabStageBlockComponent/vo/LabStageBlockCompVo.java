package com.iecube.community.model.EMDV4.LabStageBlockComponent.vo;

import com.iecube.community.model.EMDV4.LabComponent.vo.LabComponentVo;
import lombok.Data;

@Data
public class LabStageBlockCompVo {
    private Long id;
    private Long blockId;
    private Long componentId;
    private Integer order;
    private LabComponentVo labComponentVo;
}
