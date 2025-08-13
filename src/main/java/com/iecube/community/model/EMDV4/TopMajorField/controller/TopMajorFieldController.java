package com.iecube.community.model.EMDV4.TopMajorField.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.TopMajorField.entity.MajorField;
import com.iecube.community.model.EMDV4.TopMajorField.service.TopMajorFieldService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emdv4/mf/")
public class TopMajorFieldController extends BaseController {

    @Autowired
    private TopMajorFieldService topMajorFieldService;

    @GetMapping("/all_mf")
    public JsonResult<List<MajorField>> getAll(){
        return new JsonResult<>(OK, topMajorFieldService.getAllMajorField());
    }
}
