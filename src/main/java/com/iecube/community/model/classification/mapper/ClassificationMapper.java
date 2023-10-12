package com.iecube.community.model.classification.mapper;

import com.iecube.community.model.classification.entity.Classification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassificationMapper {
    Integer insert(Classification classification);

    Integer update(Classification classification);

    Integer delete(Integer id);

    Classification findById(Integer id);

    Classification findNameWithParenId(Integer parentId, String name);

    List<Classification> findByParentId(Integer parentId);



}
