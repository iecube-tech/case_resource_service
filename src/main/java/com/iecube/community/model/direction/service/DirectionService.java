package com.iecube.community.model.direction.service;

import com.iecube.community.model.direction.entity.Direction;

import java.util.List;

/**
 * direction 业务层接口
 */
public interface DirectionService {
    void addDirection(Direction direction, Integer lastModifiedUser);

    void updateDirection(Direction direction, Integer lastModifiedUser);

    void deleteDirection(Integer id, Integer lastModifiedUser);

    Direction findDirectionByID(Integer id);

    Direction findDirectionByName(String name);

    List<Direction> findAllDirection();
}
