package com.iecube.community.model.elaborate_md.sectionalization.service;

import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.qo.SectionalizationQo;
import com.iecube.community.model.elaborate_md.sectionalization.vo.SectionVo;

import java.util.List;

public interface SectionalizationService {

    void createSectionalization(SectionalizationQo sectionalizationQo);

    void deleteSectionalization(SectionalizationQo sectionalizationQo);

    void updateSectionalizationSort(List<Sectionalization> list);

    List<Sectionalization> getSectionalizationByLabModeId(long labModelId);

    List<SectionVo> getSectionVoByLabModelId(long labModelId);

}
