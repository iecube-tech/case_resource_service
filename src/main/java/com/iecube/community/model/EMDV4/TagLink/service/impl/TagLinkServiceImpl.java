package com.iecube.community.model.EMDV4.TagLink.service.impl;

import com.iecube.community.model.EMDV4.TagLink.entity.TagLink;
import com.iecube.community.model.EMDV4.TagLink.mapper.TagLinkMapper;
import com.iecube.community.model.EMDV4.TagLink.service.TagLinkService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagLinkServiceImpl implements TagLinkService {

    @Autowired
    private TagLinkMapper tagLinkMapper;


    @Override
    public List<TagLink> getByTagId(long tagId) {
        return tagLinkMapper.getByTagId(tagId);
    }

    @Override
    public List<TagLink> addTagLink(TagLink tagLink) {
        if(tagLink==null||tagLink.getTagId()==null){
            throw new InsertException("未选择目标标签");
        }
        int res = tagLinkMapper.insert(tagLink);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return this.getByTagId(tagLink.getTagId());
    }

    @Override
    public List<TagLink> updateTagLink(TagLink tagLink) {
        if(tagLink==null||tagLink.getId()==null){
            throw new UpdateException("缺少必要参数");
        }
        TagLink exists = tagLinkMapper.getById(tagLink.getId());
        if(exists==null){
            throw new UpdateException("未找到相关数据");
        }
        int res = tagLinkMapper.updateById(tagLink);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return tagLinkMapper.getByTagId(tagLink.getTagId());
    }

    @Override
    public List<TagLink> deleteTagLink(long id) {
        TagLink exists = tagLinkMapper.getById(id);
        if(exists!=null){
            Long tagId = exists.getTagId();
            tagLinkMapper.deleteById(id);
            return tagLinkMapper.getByTagId(tagId);
        }
        return null;
    }
}
