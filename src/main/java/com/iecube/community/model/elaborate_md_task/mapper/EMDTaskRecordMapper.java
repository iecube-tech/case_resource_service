package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDTaskRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EMDTaskRecordMapper {
    void insert(EMDTaskRecord record);
}
