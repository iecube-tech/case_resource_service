package com.iecube.community.model.EMDV4.TagLink.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.TagLink.entity.TagLink;
import com.iecube.community.model.EMDV4.TagLink.service.TagLinkService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/tag/link")
public class TagLinkController extends BaseController {

    @Autowired
    private TagLinkService tagLinkService;

    @GetMapping()
    public JsonResult<List<TagLink>> getTagLinks(Long tagId) {
        return new JsonResult<>(OK, tagLinkService.getByTagId(tagId));
    }

    @PostMapping("/add")
    public JsonResult<List<TagLink>> addTagLink(@RequestBody TagLink tagLink) {
        List<TagLink> res = tagLinkService.addTagLink(tagLink);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/up")
    public JsonResult<List<TagLink>> upTagLink(@RequestBody TagLink tagLink) {
        List<TagLink> res = tagLinkService.updateTagLink(tagLink);
        return new JsonResult<>(OK, res);
    }

    @DeleteMapping("/del")
    public JsonResult<List<TagLink>> deleteTagLink(@RequestBody TagLink tagLink) {
        List<TagLink> res = tagLinkService.deleteTagLink(tagLink.getId());
        return new JsonResult<>(OK, res);
    }
}
