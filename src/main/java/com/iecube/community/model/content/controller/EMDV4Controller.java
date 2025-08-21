package com.iecube.community.model.content.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4_course/")
public class EMDV4Controller extends BaseController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/up_completion")
    public JsonResult<Content> updateMdCourseCompletion(Integer id, Integer completion ){
        Integer user = currentUserId();
        contentService.contentCompletionUpdate(completion, id, user);
        Content content = contentService.findById(id);
        return new JsonResult<>(OK, content);
    }

    /**
     * 新建emdv4课程
     * @param content
     * @return
     */
    @PostMapping ("/create")
    public JsonResult<Content> addNewContent(@RequestBody Content content){
        if(content==null || content.getDeviceId()==null || content.getEmdv4Course()==null || content.getVersion()==null){
            throw new InsertException("表单提交错误");
        }
        if(content.getId()!=null){
            contentService.updateContent(content,currentUserId());
            return new JsonResult<>(OK, contentService.findById(content.getId()));
        }
        Integer lastModifiedUser = currentUserId();
        String userType = currentUserType();
        Integer id = contentService.addContent(content,userType, lastModifiedUser);
        return new JsonResult<>(OK, contentService.findById(id));
    }

    /**
     * 获取用户创建的MD课程
     * @return
     */
    @GetMapping("/emd_created")
    public JsonResult<List> getEMdCourseCreated(){
        Integer user = currentUserId();
        List<Content> contentList = contentService.getEMDV4CourseCreated(user);
        return new JsonResult<>(OK, contentList);
    }

    @PostMapping("/fourth/type/up")
    public JsonResult<Void> updateMdCourseFourthType(Integer id, String type){
        // 理实映射
        contentService.updateFourthType(id, type);
        return new JsonResult<>(OK);
    }

    @PostMapping("/up_fourth/{contentId}")
    public JsonResult<Void> mdContentUpdateFourth(@PathVariable Integer contentId, String fourth){
        Integer modifiedUser = currentUserId();
        contentService.mdContentUpdateFourth(contentId,fourth,modifiedUser);
        return new JsonResult<>(OK);
    }

}

