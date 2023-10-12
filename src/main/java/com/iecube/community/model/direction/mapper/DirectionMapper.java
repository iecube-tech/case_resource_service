package com.iecube.community.model.direction.mapper;

import com.iecube.community.model.direction.entity.Direction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DirectionMapper {

    Integer insert(Direction direction);

    Integer update(Direction direction);

    Integer delete(Integer id);

    Direction findById(Integer id);

    Direction findByName(String name);

    List<Direction> findAll();

}
