package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTMSection;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDSTMSectionMapper {
    int BatchAdd(List<EMDSTMSection> list);

    List<EMDTaskSectionVo> getBySTM(Long stmId);

    int upStatus(Long STMSId, int status);
}
