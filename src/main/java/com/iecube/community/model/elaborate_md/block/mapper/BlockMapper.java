package com.iecube.community.model.elaborate_md.block.mapper;

import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.entity.BlockDetail;
import com.iecube.community.model.elaborate_md.block.vo.BlockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlockMapper {
    int createBlock(Block block);

    int delBlock(long id);

    Block getById(long id);

    List<Block> getBySectionId(long parentId);

    int batchUpSort(List<Block> list);

    // blockDetail
    int createBlockDetail(BlockDetail blockDetail);
    BlockDetail getBlockDetailByBlock(long blockId);
    int upBlockDetail(BlockDetail blockDetail);

    // block & blockDetail
    List<BlockVo> getBlockVoBySection(long sectionId);

    BlockVo getBlockVoByBlockId(long id);
}
