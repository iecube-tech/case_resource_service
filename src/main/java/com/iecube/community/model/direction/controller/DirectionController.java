package com.iecube.community.model.direction.controller;

import com.iecube.community.basecontroller.direction.DirectionBaseController;
import com.iecube.community.model.direction.entity.Direction;
import com.iecube.community.model.direction.service.DirectionService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/direction")
public class DirectionController extends DirectionBaseController {

    @Autowired
    private DirectionService directionService;

    @PostMapping("/add")
    public JsonResult<Void> addDirection(Direction direction, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        directionService.addDirection(direction, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @PostMapping("/update")
    public  JsonResult<Void> updateDirection(Direction direction, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        directionService.updateDirection(direction, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @DeleteMapping("/delete")
    public JsonResult<Void> deleteDirection(Integer id, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        directionService.deleteDirection(id, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @GetMapping("/get_by_id")
    public JsonResult<Direction> findDirectionByID(Integer id){
        Direction direction = directionService.findDirectionByID(id);
        return new JsonResult<>(OK, direction);
    }

    @GetMapping("/get_by_name")
    public JsonResult<Direction> findDirectionByName(String name){
        Direction direction = directionService.findDirectionByName(name);
        return new JsonResult<>(OK, direction);
    }

    @GetMapping("/get_all")
    public JsonResult<List> findAllDirection(){
        List<Direction> directions = directionService.findAllDirection();
        return new JsonResult<List>(OK, directions);
    }

}
