package com.iecube.community.model.EMDV4.Tag.service.impl;

import com.iecube.community.model.EMDV4.BookTarget.entity.BookTarget;
import com.iecube.community.model.EMDV4.BookTarget.mapper.BookTargetMapper;
import com.iecube.community.model.EMDV4.Tag.entity.BLTTag;
import com.iecube.community.model.EMDV4.Tag.mapper.BLTTagMapper;
import com.iecube.community.model.EMDV4.Tag.qo.BLTTagQo;
import com.iecube.community.model.EMDV4.Tag.service.BLTTagService;
import com.iecube.community.model.EMDV4.Tag.vo.BLTTagVo;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BLTTagServiceImpl implements BLTTagService {

    @Autowired
    private BLTTagMapper tagMapper;

    @Autowired
    private BookTargetMapper bookTargetMapper;


    @Override
    public List<BLTTagVo> getBLTTagVoByBookId(Long bookId) {
        return tagMapper.getTagVoByBookId(bookId);
    }

    @Override
    public List<BLTTagVo> addBookTag(BLTTagQo qo) {
        BookTarget bookTarget = bookTargetMapper.selectByBookIdAndTargetId(qo.getBookId(), qo.getTargetId());
        if(bookTarget == null){
            throw new InsertException("没有找到课程对应的课程目标");
        }
        BLTTag tag = qoToTag(qo);
        tag.setBLTId(bookTarget.getId());
        int res = tagMapper.insert(tag);
        if(res != 1){
            throw new InsertException("新增数据异常");
        }
        return tagMapper.getTagVoByBookId(qo.getBookId());
    }

    @Override
    public List<BLTTagVo> UpdateBookTag(BLTTagQo qo) {
        BookTarget bookTarget = bookTargetMapper.selectByBookIdAndTargetId(qo.getBookId(), qo.getTargetId());
        if(bookTarget == null){
            throw new UpdateException("没有找到课程对应的课程目标");
        }
        BLTTag tag = qoToTag(qo);
        tag.setBLTId(bookTarget.getId());
        int res = tagMapper.update(tag);
        if(res != 1){
            throw new UpdateException("新增数据异常");
        }
        return tagMapper.getTagVoByBookId(qo.getBookId());
    }

    @Override
    public List<BLTTagVo> deleteBookTag(Long tagId) {
        BLTTag bltTag = tagMapper.getById(tagId);
        if(bltTag==null){
            throw new DeleteException("没有找到相应数据");
        }
        BookTarget bookTarget = bookTargetMapper.getById(bltTag.getBLTId());
        int res = tagMapper.delete(tagId);
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
        return tagMapper.getTagVoByBookId(bookTarget.getBookId());
    }

    private BLTTag qoToTag(BLTTagQo qo) {
        BLTTag tag = new BLTTag();
        tag.setBLTId(qo.getId()==null?null:qo.getId());
        tag.setName(qo.getName());
        tag.setAbility(qo.getAbility());
        tag.setDescription(qo.getDescription());
        tag.setStyle(qo.getStyle());
        tag.setConfig(qo.getConfig());
        tag.setPayload(qo.getPayload());
        return tag;
    }
}
