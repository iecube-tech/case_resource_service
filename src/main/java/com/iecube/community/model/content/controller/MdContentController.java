package com.iecube.community.model.content.controller;

import com.iecube.community.basecontroller.content.ContentBaseController;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.util.JsonResult;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/md_content")
public class MdContentController extends ContentBaseController {
    /**
     * 更新当前的步骤 0,1,2,3,4
     * 创建md课程 基本信息，图片，关联设备，实验指导书 ==> 0
     * 课程设计 ==> 1
     * 实验设计 ==> 2
     * 知识点设计 ==> 3
     * 附件资源 ==> 4
     * 完成 ==> 6
     * 创建的MD课程
     */

    @Autowired
    private ContentService contentService;

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/curl")
    public JsonResult<String> curl(){
        return new JsonResult<>(OK, "ok");
    }

    @PostMapping("/up_completion")
    public JsonResult<Content> updateMdCourseCompletion(Integer id, Integer completion ){
        Integer user = currentUserId();
        contentService.contentCompletionUpdate(completion, id, user);
        Content content = contentService.findById(id);
        return new JsonResult<>(OK, content);
    }

    /**
     * 新建md课程
     * @param content
     * @return
     */
    @PostMapping ("/create")
    public JsonResult<Content> addNewContent(@RequestBody Content content){
        if(content==null || content.getMdCourse() == null || content.getDeviceId()==null){
            throw new InsertException("表单提交错误");
        }
        Integer lastModifiedUser = currentUserId();
        String userType = currentUserType();
        Integer id = contentService.addContent(content,userType, lastModifiedUser);
        Content newContent = contentService.findById(id);
        return new JsonResult<>(OK, newContent);
    }


    /**
     * 获取用户创建的MD课程
     * @return
     */
    @GetMapping ("/md_created")
     public JsonResult<List> getMdCourseCreated(){
        Integer user = currentUserId();
        List<Content> contentList = contentService.getMdCourseCreated(user);
        return new JsonResult<>(OK, contentList);
     }
}
