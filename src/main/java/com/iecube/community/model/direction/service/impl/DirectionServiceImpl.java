package com.iecube.community.model.direction.service.impl;

import com.iecube.community.model.auth.mapper.UserMapper;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.entity.Direction;
import com.iecube.community.model.direction.mapper.DirectionMapper;
import com.iecube.community.model.direction.service.DirectionService;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.direction.service.ex.DirectionDuplicateException;
import com.iecube.community.model.direction.service.ex.DirectionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DirectionServiceImpl implements DirectionService {

    @Autowired
    private DirectionMapper directionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addDirection(Direction direction, Integer lastModifiedUser) {
        /**增加权限， 管理员 判断lastModifiedUser 的 typeId 是否为 1 **/
        Integer userType = userMapper.findTypeIdById(lastModifiedUser);
        if (userType != 1){
            throw new PermissionDeniedException("无权添加");
        }

        Direction oldDirection = directionMapper.findByName(direction.getName());

        /**判断是否存在，抛出异常**/
        if (oldDirection != null){
            throw new DirectionDuplicateException("产品方向已存在");
        }
        direction.setCreator(lastModifiedUser);
        direction.setCreateTime(new Date());
        direction.setLastModifiedTime(new Date());
        direction.setLastModifiedUser(lastModifiedUser);
        Integer rows = directionMapper.insert(direction);
        if (rows != 1){
            throw new InsertException("添加产品方向产生未知异常");
        }
    }

    @Override
    public void updateDirection(Direction direction, Integer lastModifiedUser) {
        /**修改权限， 管理员 判断lastModifiedUser 的 typeId 是否为 1 **/
        Integer userType = userMapper.findTypeIdById(lastModifiedUser);
        if (userType != 1){
            throw new PermissionDeniedException("无权修改");
        }
        Direction oldDirection = directionMapper.findById(direction.getId());
        /**判断是否存在，抛出异常**/
        if (oldDirection == null){
            throw new DirectionNotFoundException("产品方向不存在");
        }
        direction.setCreator(oldDirection.getCreator());
        direction.setCreateTime(oldDirection.getCreateTime());
        direction.setLastModifiedUser(lastModifiedUser);
        direction.setLastModifiedTime(new Date());
        /**判断修改后是否冲突**/
        Direction exitsDirection = directionMapper.findByName(direction.getName());

        /**判断是否存在，抛出异常**/
        if (exitsDirection != null){
            throw new DirectionDuplicateException("产品方向已存在，修改无效");
        }
        Integer rows = directionMapper.update(direction);
        if (rows != 1){
            throw new UpdateException("更新数据异常");
        }

    }

    @Override
    public void deleteDirection(Integer id, Integer lastModifiedUser) {
        Direction direction = directionMapper.findById(id);
        /**判断是否存在，抛出异常**/
        if (direction == null){
            throw new DirectionNotFoundException("产品方向不存在");
        }
        /**删除权限， 管理员 判断lastModifiedUser 的 typeId 是否为 1 **/
        Integer userType = userMapper.findTypeIdById(lastModifiedUser);
        if (userType != 1){
            throw new PermissionDeniedException("无权删除");
        }
        /**删除**/
        Integer rows =  directionMapper.delete(id);
        if(rows != 1){
            throw new DeleteException("删除数据产生未知异常");
        }
        /** 删除后要删除对应的产品分类 调用ClassificationService的删除**/

    }

    @Override
    public Direction findDirectionByID(Integer id) {
        Direction direction = directionMapper.findById(id);
        if (direction == null){
            throw new DirectionNotFoundException("产品方向不存在");
        }
        return direction;
    }

    @Override
    public Direction findDirectionByName(String name) {
        Direction direction = directionMapper.findByName(name);
        if (direction == null){
            throw new DirectionNotFoundException("产品方向不存在");
        }
        return direction;
    }

    @Override
    public List<Direction> findAllDirection() {
        List<Direction> directions = directionMapper.findAll();
        return directions;
    }
}
