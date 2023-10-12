package com.iecube.community.model.content.service;

import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.entity.taskTemplates;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.entity.ResourceVo;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;

import java.util.List;

public interface ContentService {
    Integer addContent(Content content, String userType, Integer lastModifiedUser);

    void contentUpdateCover(Integer contentId, Resource resource, Integer lastModifiedUser);

    void updateContent(Content content, Integer lastModifiedUser);

    void deleteContent(Integer id, Integer lastModifiedUser);

    void contentCompletionUpdate(Integer completion, Integer contentId, Integer lastModifiedUser);

    void contentUpdatePoints(Integer id, List<Integer> modules, Integer lastModifiedUser);

    List<Content> getTeacherCreate(Integer teacherId);
    Content findById(Integer id);
    String findGuidanceById(Integer id);

    void updateGuidanceById(Integer id, String guidance, Integer lastModifiedUser);
    List<ResourceVo> findResourcesById(Integer id);

    List<Content> findByParentId(Integer parentId);

    List<Content> findNotDelByParentId(Integer parentId);

    void restore (Integer id,  Integer latModifiedUser);

    List<Content> findAll();

    List<Content> findByTeacherId(Integer teacherId);

    List<TaskTemplateDto> findTaskTemplatesByContentId(Integer contentId);

    void CaseAccredit(Integer teacherId,List<Integer> contentIds);
}
