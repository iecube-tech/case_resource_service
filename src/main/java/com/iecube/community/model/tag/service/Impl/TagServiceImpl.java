package com.iecube.community.model.tag.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.entity.TagTemplates;
import com.iecube.community.model.tag.mapper.TagMapper;
import com.iecube.community.model.tag.service.TagService;
import com.iecube.community.model.tag.vo.TeacherProjectTagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;


    @Override
    public List<Tag> getTagsByTeacherProject(Integer projectId, Integer teacherId) {
        List<Tag> tags = tagMapper.getTagsByTeacherProject(projectId, teacherId);
        return tags;
    }

    @Override
    public List<TagTemplates> ContentTaskTagTemplate(Integer contentId, Integer taskNum) {
        List<TagTemplates> tagTemplates = tagMapper.ContentTaskTagTemplate(contentId,taskNum);
        return tagTemplates;
    }

    @Override
    public void addTagTemplate(TagTemplates tagTemplate) {
        Integer row = tagMapper.addTagTemplates(tagTemplate);
        if (row!=1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public void modifyTagTemplate(TagTemplates tagTemplate) {
        Integer row = tagMapper.modifyTagTemplates(tagTemplate);
        if(row != 1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void deleteTagTemplate(Integer tagTemplateId) {
        Integer row = tagMapper.deleteTagTemplate(tagTemplateId);
        if (row != 1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void tagTemplateToTag(Integer contentId, Integer projectId, Integer teacherId) {
        List<TagTemplates> tagTemplates = tagMapper.contentTagTemplates(contentId);
        for(TagTemplates tagTemplate:tagTemplates){
            Tag tag = new Tag();
            tag.setName(tagTemplate.getName());
            tag.setSuggestion(tagTemplate.getSuggestion());
            tag.setTaskNum(tagTemplate.getTaskNum());
            tag.setTeacherId(teacherId);
            tag.setProjectId(projectId);
            Integer row = tagMapper.addTag(tag);
            if(row!=1){
                throw new InsertException("插入tag数据异常");
            }
        }
    }

    @Override
    public void deleteTagByProjectId(Integer projectId) {

    }

    @Override
    public List<TeacherProjectTagVo> getTeacherProjectTags(Integer teacherId, Integer projectId) {
        List<TeacherProjectTagVo> teacherProjectTags = tagMapper.getTeacherProjectTags(teacherId, projectId);
        return teacherProjectTags;
    }

    @Override
    public void addTag(Tag tag) {
        Integer row = tagMapper.addTag(tag);
        if (row != 1){
            throw new InsertException("插入tag数据异常");
        }
    }

    @Override
    public void modifyTag(Tag tag) {
        Integer row = tagMapper.modifyTag(tag);
        if (row != 1){
            throw new UpdateException("更新tag数据异常");
        }
    }

    @Override
    public void deleteTag(Integer tagId) {
        Integer row = tagMapper.deleteTag(tagId);
        if(row != 1){
            throw new DeleteException("删除数据异常");
        }
    }
}
