package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTMSBlock;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskBlockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDSTMSBlockMapper {
    int BatchAdd(List<EMDSTMSBlock> list);

    List<EMDTaskBlockVo> batchGetBySTMSId(List<Long> list);

    int updatePayload(EMDSTMSBlock block);
}
