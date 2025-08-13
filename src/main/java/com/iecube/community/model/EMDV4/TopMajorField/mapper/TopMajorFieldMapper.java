package com.iecube.community.model.EMDV4.TopMajorField.mapper;

import com.iecube.community.model.EMDV4.TopMajorField.entity.MajorField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TopMajorFieldMapper {
    int insert(MajorField record);

    int deleteById(MajorField record);

    int updateNameById(MajorField newMajorField);

    MajorField getById(Long id);

    List<MajorField> getAll();
}
