package com.iecube.community.model.tag.mapper;

import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.entity.TagTemplates;
import com.iecube.community.model.tag.vo.TeacherProjectTagVo;
import com.iecube.community.model.task.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> getTagsByPSTId(Integer pstId);
    List<Tag> getTagsByTeacherProject(Integer projectId, Integer teacherId);

    List<TagTemplates> ContentTaskTagTemplate(Integer contentId, Integer taskNum);

    Integer addTagTemplates(TagTemplates tagTemplate);

    Integer modifyTagTemplates(TagTemplates tagTemplate);

    Integer deleteTagTemplate(Integer id);

    Integer addTagToPST(Integer pstId,Integer tagId);

    Integer deletePSTTag(Integer pstId, Integer tagId);

    List<TagTemplates> contentTagTemplates(Integer contentId);

    Integer addTag(Tag tag);

    Integer modifyTag(Tag tag);

    Integer deleteTag(Integer id);

    List<TeacherProjectTagVo> getTeacherProjectTags(Integer teacherId, Integer projectId);




}
