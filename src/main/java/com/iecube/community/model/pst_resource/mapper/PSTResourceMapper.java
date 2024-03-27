package com.iecube.community.model.pst_resource.mapper;

import com.iecube.community.model.pst_resource.entity.PSTResource;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSTResourceMapper {
    List<PSTResource> getPSTResourcesByPSTId(Integer PSTId);

    Integer updatePSTResource(PSTResource pstResource);

    PSTResource getById(Integer id);

    Integer add(PSTResource pstResource);

    Integer deleteById(Integer id);
}
