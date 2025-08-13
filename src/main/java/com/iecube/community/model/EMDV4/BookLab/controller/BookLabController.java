package com.iecube.community.model.EMDV4.BookLab.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.BookLab.service.BookLabService;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emdv4/book_lab/")
public class BookLabController extends BaseController {
    @Autowired
    private BookLabService bookLabService;

    @GetMapping("/book")
    public JsonResult<List<BookLabCatalog>> getBookLabCatalog() {
        return new JsonResult<>(OK, bookLabService.getRootNodes());
    }

    @GetMapping("/children")
    public JsonResult<List<BookLabCatalog>> getChildren(Long parentId) {
        return new JsonResult<>(OK, bookLabService.getChildrenByParentId(parentId));
    }

    @PostMapping("/add")
    public JsonResult<List<BookLabCatalog>> add(@RequestBody BookLabCatalog bookLabCatalog) {
        return new JsonResult<>(OK, bookLabService.createBookLabCatalog(bookLabCatalog));
    }

    @PostMapping("/update")
    public JsonResult<BookLabCatalog> update(@RequestBody BookLabCatalog bookLabCatalog) {
        return new JsonResult<>(OK, bookLabService.updateBookLabCatalog(bookLabCatalog));
    }


    @DeleteMapping("/del")
    public JsonResult<List<BookLabCatalog>> del(Long id) {
        log.warn("删除实验指导书数据： 用户：{}, {}", currentUserId(), currentUserEmail());
        return new JsonResult<>(OK, bookLabService.deleteById(id));
    }

}
