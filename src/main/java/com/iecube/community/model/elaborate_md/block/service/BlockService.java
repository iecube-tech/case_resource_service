package com.iecube.community.model.elaborate_md.block.service;

import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.qo.BlockQo;

import java.util.List;

public interface BlockService {
    void createBlock(BlockQo blockQo);

    void delBlock(Block block);

    void batchUpBlockSort(List<Block> list);

    List<Block> getBlockListBySection(long sectionId);
}
