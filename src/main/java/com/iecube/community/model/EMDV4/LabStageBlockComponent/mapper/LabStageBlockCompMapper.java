package com.iecube.community.model.EMDV4.LabStageBlockComponent.mapper;

import com.iecube.community.model.EMDV4.LabStageBlockComponent.entity.LabStageBlockComp;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.vo.LabStageBlockCompVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabStageBlockCompMapper {
    List<LabStageBlockCompVo> getBlockCompVos(Long blockId);

    LabStageBlockCompVo getById(Long id);

    int insert(LabStageBlockComp record);

    int delete(Long id);

    int updateOrderBatch(List<LabStageBlockComp> list);
}
