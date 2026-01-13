package com.iecube.community.model.EMDV4.TagLink.mapper;

import com.iecube.community.model.EMDV4.TagLink.entity.TagLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagLinkMapper {
    int insert(TagLink tagLink);
    int updateById(TagLink tagLink);
    int deleteById(Long id);
    TagLink getById(Long id);
    List<TagLink> getByTagId(Long tagId);
    List<TagLink> getByTagIds(List<Long> tagIds);
}
