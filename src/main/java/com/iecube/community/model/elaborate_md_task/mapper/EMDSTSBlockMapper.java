package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTSBlock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDSTSBlockMapper {
    int BatchAdd(List<EMDSTSBlock> list);
}
