package com.iecube.community.model.classification.service;

import com.iecube.community.model.classification.entity.Classification;

import java.util.List;

/**
 * classification 业务层接口
 */
public interface ClassificationService {
    void insert(Classification classification, Integer lastModifiedUser);

    void update(Classification classification, Integer lastModifiedUser);

    void delete(Integer id, Integer lastModifiedUser);

    Classification findById(Integer id);

    List<Classification> findByParentId(Integer parentId);
}
