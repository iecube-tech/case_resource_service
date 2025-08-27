package com.iecube.community.model.EMDV4Project.EMDV4_component.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ComponentMapper {
    int insert(EMDV4Component record);
    int batchInsert(List<EMDV4Component> records);
}
