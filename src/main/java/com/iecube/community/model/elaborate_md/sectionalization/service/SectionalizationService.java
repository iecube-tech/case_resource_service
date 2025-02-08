package com.iecube.community.model.elaborate_md.sectionalization.service;

import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.qo.SectionalizationQo;

import java.util.List;

public interface SectionalizationService {

    void createSectionalization(SectionalizationQo sectionalizationQo);

    void deleteSectionalization(long id);

    void updateSectionalizationSort(List<Sectionalization> list);

    List<Sectionalization> getSectionalizationByLabProcId(long labProcId);

}
