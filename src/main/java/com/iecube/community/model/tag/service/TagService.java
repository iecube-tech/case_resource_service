package com.iecube.community.model.tag.service;

import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.entity.TagTemplates;
import com.iecube.community.model.tag.vo.TeacherProjectTagVo;

import java.util.List;

public interface TagService {
    List<Tag> getTagsByTeacherProject(Integer projectId, Integer teacherId);

    List<TagTemplates> ContentTaskTagTemplate(Integer contentId, Integer taskNum);

    void addTagTemplate(TagTemplates tagTemplate);

    void modifyTagTemplate(TagTemplates tagTemplate);

    void deleteTagTemplate(Integer tagTemplateId);

    void tagTemplateToTag(Integer contentId, Integer projectId, Integer teacherId);

    void deleteTagByProjectId(Integer projectId);

    List<TeacherProjectTagVo> getTeacherProjectTags(Integer teacherId, Integer projectId);

    void addTag(Tag tag);

    void modifyTag(Tag tag);

    void deleteTag(Integer tagId);
}
