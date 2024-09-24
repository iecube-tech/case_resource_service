package com.iecube.community.model.classification.controller;

import com.iecube.community.basecontroller.classification.ClassificationBaseController;
import com.iecube.community.model.classification.entity.Classification;
import com.iecube.community.model.classification.service.ClassificationService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("class")
public class ClassificationController extends ClassificationBaseController {

    @Autowired
    private ClassificationService classificationService;

    @PostMapping("/add")
    JsonResult<Void> add(Classification classification){
        Integer lastModifiedUser = currentUserId();
        classificationService.insert(classification,lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @PostMapping("/update")
    JsonResult<Void> update(Classification classification){
        Integer lastModifiedUser = currentUserId();
        classificationService.update(classification, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @DeleteMapping("/delete")
    JsonResult<Void> delete(Integer id){
        Integer lastModifiedUser = currentUserId();
        classificationService.delete(id, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    @GetMapping("/by_id")
    JsonResult<Classification> findById(Integer id){
        Classification classification = classificationService.findById(id);
        return new JsonResult<>(OK, classification);
    }

    @GetMapping("/by_parent")
    JsonResult<List> findByParentId(Integer parentId){
        List<Classification> classifications = classificationService.findByParentId(parentId);
        return new JsonResult<>(OK, classifications);
    }
}
