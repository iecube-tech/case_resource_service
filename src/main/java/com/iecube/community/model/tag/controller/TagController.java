package com.iecube.community.model.tag.controller;

import com.iecube.community.basecontroller.tag.TagBaseController;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.entity.TagTemplates;
import com.iecube.community.model.tag.service.TagService;
import com.iecube.community.model.tag.vo.TeacherProjectTagVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController extends TagBaseController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tp")
    public JsonResult<List> getTeacherProjectTag(Integer projectId, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<Tag> tags = tagService.getTagsByTeacherProject(projectId,teacherId);
        return new JsonResult<>(OK,tags);
    }

    @GetMapping("/case_task")
    public JsonResult<List> getContentTaskTagTemplates(Integer contentId, Integer taskNum){
        List<TagTemplates> tagTemplates = tagService.ContentTaskTagTemplate(contentId,taskNum);
        return new JsonResult<>(OK, tagTemplates);
    }
    @PostMapping("/add_tag_templates")
    public JsonResult<Void> addTagTemplates(@RequestBody TagTemplates tagTemplate){
        tagService.addTagTemplate(tagTemplate);
        return new JsonResult<>(OK);
    }

    @PostMapping("/modify_tag_templates")
    public JsonResult<Void> modifyTagTemplates(@RequestBody TagTemplates tagTemplate){
        tagService.modifyTagTemplate(tagTemplate);
        return new JsonResult<>(OK);
    }

    @GetMapping("delete_tag_template")
    public JsonResult<Void> deleteTagTemplate(Integer id){
        tagService.deleteTagTemplate(id);
        return new JsonResult<>(OK);
    }

    @GetMapping("teacher_project_tags")
    public JsonResult<List> getTeacherProjectTags(Integer projectId, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<TeacherProjectTagVo> teacherProjectTags = tagService.getTeacherProjectTags(teacherId, projectId);
        return new JsonResult<>(OK, teacherProjectTags);
    }

    @PostMapping("/add_tag")
    public JsonResult<Void> addTag(@RequestBody Tag tag, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        tag.setTeacherId(teacherId);
        tagService.addTag(tag);
        return new JsonResult<>(OK);
    }

    @PostMapping("/modify_tag")
    public JsonResult<Void> modifyTag(@RequestBody Tag tag, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        tag.setTeacherId(teacherId);
        tagService.modifyTag(tag);
        return new JsonResult<>(OK);
    }

    @GetMapping("/delete_tag")
    public JsonResult<Void> deleteTag(Integer id){
        tagService.deleteTag(id);
        return new JsonResult<>(OK);
    }
}
