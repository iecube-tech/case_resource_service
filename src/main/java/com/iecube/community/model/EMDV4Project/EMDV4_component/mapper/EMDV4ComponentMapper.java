package com.iecube.community.model.EMDV4Project.EMDV4_component.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ComponentMapper {
    int insert(EMDV4Component record);
    int batchInsert(List<EMDV4Component> records);
    List<EMDV4Component> getByBlockId(String blockId);
    EMDV4Component getById(String id);
    int updateStatus(String id, int status);
    int updatePayload(String id, String payload);
    int updateScore(String id, double score, double scoreProportion);
}
