package com.iecube.community.model.EMDV4.TagLink.service;

import com.iecube.community.model.EMDV4.TagLink.entity.TagLink;

import java.util.List;

public interface TagLinkService {

    List<TagLink> getByTagId(long tagId);

    List<TagLink> addTagLink(TagLink tagLink);

    List<TagLink> updateTagLink(TagLink tagLink);

    List<TagLink> deleteTagLink(long id);

}
