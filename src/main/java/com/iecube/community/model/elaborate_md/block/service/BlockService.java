package com.iecube.community.model.elaborate_md.block.service;

import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.entity.BlockDetail;
import com.iecube.community.model.elaborate_md.block.qo.BlockQo;
import com.iecube.community.model.elaborate_md.block.vo.BlockVo;

import java.util.List;

public interface BlockService {
    BlockVo createBlock(BlockQo blockQo);

    void delBlock(Block block);

    void batchUpBlockSort(List<Block> list);

    List<Block> getBlockListBySection(long sectionId);

    // block & blockDetail
    BlockVo getBlockVoById(long blockId);
    List<BlockVo> getBlockVoListBySection(long sectionId);


    //blockDetail
    BlockDetail getBlockDetailByBlock(long blockId);
    void upBlockDetail(BlockDetail blockDetail);
}
