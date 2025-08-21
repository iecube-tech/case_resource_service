package com.iecube.community.model.EMDV4.Tag.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.Tag.qo.BLTTagQo;
import com.iecube.community.model.EMDV4.Tag.service.BLTTagService;
import com.iecube.community.model.EMDV4.Tag.vo.BLTTagVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/book_tag/")
public class BLTTagController extends BaseController {

    @Autowired
    private BLTTagService tagService;

    @GetMapping("/get_tag")
    public JsonResult<List<BLTTagVo>> bookTag(Long bookId){
        return new JsonResult<>(OK, tagService.getBLTTagVoByBookId(bookId));
    }

    @PostMapping("/add_tag")
    public JsonResult<List<BLTTagVo>> bookAddTag(@RequestBody BLTTagQo qo){
        return new JsonResult<>(OK, tagService.addBookTag(qo));
    }

    @PostMapping("/update_tag")
    public JsonResult<List<BLTTagVo>> bookUpdateTag(@RequestBody BLTTagQo qo){
        return new JsonResult<>(OK, tagService.UpdateBookTag(qo));
    }

    @DeleteMapping("/del_tag")
    public JsonResult<List<BLTTagVo>> delTag(Long id){
        return new JsonResult<>(OK, tagService.deleteBookTag(id));
    }
}
