package com.iecube.community.model.louYongle.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.louYongle.entity.XiDianGrade;
import com.iecube.community.model.louYongle.service.LouYongLeService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/louyongle")
public class LouYongLeController extends BaseController {

    @Autowired
    private LouYongLeService louYongLeService;


    @PostMapping("/add")
    public JsonResult<List<XiDianGrade>> Insert(@RequestBody List<XiDianGrade> xiDianGrades) {
        louYongLeService.insert(xiDianGrades);
        return new JsonResult<>(OK,louYongLeService.getAll());
    }

    @GetMapping("/all")
    public JsonResult<List<XiDianGrade>> getAll() {
        return new JsonResult<>(OK,louYongLeService.getAll());
    }
}
