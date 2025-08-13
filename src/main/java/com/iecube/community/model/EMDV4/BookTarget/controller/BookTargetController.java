package com.iecube.community.model.EMDV4.BookTarget.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.BookTarget.entity.BookTarget;
import com.iecube.community.model.EMDV4.BookTarget.service.BookTargetService;
import com.iecube.community.model.EMDV4.BookTarget.vo.BookTargetVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emdv4/book_target/")
public class BookTargetController extends BaseController {

    @Autowired
    private BookTargetService bookTargetService;

    @PostMapping("/add_target")
    public JsonResult<BookTargetVo> addBookTarget(@RequestBody BookTarget bookTarget) {
        return new JsonResult<>(OK, bookTargetService.bookAddTarget(bookTarget.getBookId(), bookTarget.getTargetId()));
    }

    @GetMapping("/get_target")
    public JsonResult<BookTargetVo> getBookTarget(Long bookId) {
        return new JsonResult<>(OK, bookTargetService.getBookTargets(bookId));
    }

}
