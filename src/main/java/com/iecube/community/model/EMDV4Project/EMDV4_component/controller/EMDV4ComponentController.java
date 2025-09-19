package com.iecube.community.model.EMDV4Project.EMDV4_component.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.qo.PayloadQo;
import com.iecube.community.model.EMDV4Project.EMDV4_component.service.EMDV4ComponentService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emdv4/component")
public class EMDV4ComponentController extends BaseController {
    @Autowired
    private EMDV4ComponentService  emdV4ComponentService;

    @PostMapping("/up/status/{id}")
    public JsonResult<EMDV4Component> updateStatus(@PathVariable String id, Integer status) {
        return new JsonResult<>(OK, emdV4ComponentService.updateStatus(id, status));
    }

    @PostMapping("/up/payload/{id}")
    public JsonResult<EMDV4Component> updatePayload(@PathVariable String id, @RequestBody PayloadQo payload) {
//        System.out.println(payload);
        return new JsonResult<>(OK, emdV4ComponentService.updatePayload(id, payload.getPayload()));
    }

    @PostMapping("/up/score/{id}")
    public JsonResult<EMDV4Component> updateScore(@PathVariable String id, Double score) {
        return new JsonResult<>(OK, emdV4ComponentService.updateScore(id, score));
    }

    @PostMapping("/check/score/{id}")
    public JsonResult<EMDV4Component> checkScore(@PathVariable String id, Double score) {
        return new JsonResult<>(OK, emdV4ComponentService.checkScore(id, score));
    }
}
