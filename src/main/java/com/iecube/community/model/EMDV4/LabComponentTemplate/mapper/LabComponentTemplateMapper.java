package com.iecube.community.model.EMDV4.LabComponentTemplate.mapper;

import com.iecube.community.model.EMDV4.LabComponent.vo.LabComponentVo;
import com.iecube.community.model.EMDV4.LabComponentTemplate.entity.LabComponentTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabComponentTemplateMapper {
    int insert(LabComponentTemplate record);

    LabComponentVo selectByLabIdAndComponent(Long labId, Long component);

    List<LabComponentVo> selectByLabId(Long labId);

    List<LabComponentVo> selectByLabIdAndType(Long labId, String type);

    void deleteByLabIdAndComponent(Long labId, Long component);


}
