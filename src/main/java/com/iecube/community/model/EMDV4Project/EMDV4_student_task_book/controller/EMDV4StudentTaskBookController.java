package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.qo.BlockTimeQo;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emdv4/block/")
public class EMDV4StudentTaskBookController extends BaseController {

    @Autowired
    private EMDV4StudentTaskBookService emdV4StudentTaskBookService;

    @PostMapping("/up/status/{id}")
    public JsonResult<EMDV4StudentTaskBook> upStatus(@PathVariable String id, Integer status) {
        return new JsonResult<>(OK, emdV4StudentTaskBookService.updateStatus(id, status));
    }

    @PostMapping("/up/score/{id}")
    public JsonResult<EMDV4StudentTaskBook> upScore(@PathVariable String id, Double score) {
        return new JsonResult<>(OK, emdV4StudentTaskBookService.updateScore(id, score));
    }

    @PostMapping("/up/time")
    public JsonResult<EMDV4StudentTaskBook> upTime(@RequestBody BlockTimeQo blockTimeQo) {
        EMDV4StudentTaskBook res = emdV4StudentTaskBookService.updateBlockTime(
            blockTimeQo.getId(),
            blockTimeQo.getStartTime(),
            blockTimeQo.getEndTime()
        );
        return new JsonResult<>(OK, res);
    }
}
