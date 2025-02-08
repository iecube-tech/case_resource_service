package com.iecube.community.model.elaborate_md.block.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.mapper.BlockMapper;
import com.iecube.community.model.elaborate_md.block.qo.BlockQo;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    private BlockMapper blockMapper;


    @Override
    public void createBlock(BlockQo blockQo) {
        if(blockQo.getSectionId()==null || blockQo.getSort()==null){
            throw new InsertException("必要参数不能为空");
        }
        Block block = new Block();
        block.setParentId(blockQo.getSectionId());
        block.setSort(blockQo.getSort());
        int res = blockMapper.createBlock(block);
        if(res != 1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public void delBlock(Block block) {
        Block existBlock = blockMapper.getById(block.getId());
        if(existBlock==null){
            throw new DeleteException("数据不存在");
        }
        int res = blockMapper.delBlock(block.getId());
        if (res != 1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void batchUpBlockSort(List<Block> list) {
        if(list==null ||list.isEmpty() ){
            throw new UpdateException("参数错误");
        }
        int res = blockMapper.batchUpSort(list);
        if(res != list.size()){
            throw new UpdateException("更新数据异常，参数长度与结果长度不符");
        }
    }

    @Override
    public List<Block> getBlockListBySection(long sectionId) {
        return blockMapper.getBySectionId(sectionId);
    }
}
