package com.iecube.community.model.resource.mapper;

import com.iecube.community.model.resource.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceMapper {
    Integer insert(Resource resource);

    Integer delete(Integer id);

    Resource getByName(String name);

    Resource getByFileName(String filename);

    Resource getById(Integer id);

}
