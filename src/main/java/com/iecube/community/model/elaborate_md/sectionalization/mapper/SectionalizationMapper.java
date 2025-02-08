package com.iecube.community.model.elaborate_md.sectionalization.mapper;

import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SectionalizationMapper {
    int createSectionalization(Sectionalization sectionalization);

    int batchUpdateSort(List<Sectionalization> list);

    int deleteSectionalization(long id);

    Sectionalization getById(long id);

    List<Sectionalization> getByLabProcId(long labProcId);
}
