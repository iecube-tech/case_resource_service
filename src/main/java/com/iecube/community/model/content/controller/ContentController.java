package com.iecube.community.model.content.controller;

import com.iecube.community.basecontroller.content.ContentBaseController;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.qo.CaseAccreditQo;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.Design;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.entity.ResourceVo;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/content")
public class ContentController extends ContentBaseController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DesignService designService;

    /**
     * 创建案例第一步  新建
     * @param content
     * @param session
     * @return
     */
    @PostMapping("/add")
    public JsonResult<Integer> addContent(@RequestBody Content content, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        String userType = getUserTypeFromSession(session);
        Integer id = contentService.addContent(content,userType, lastModifiedUser);
        return new JsonResult<>(OK, id);
    }

    /**
     * 创建案例第一步  修改案例信息
     * @param content
     * @param session
     * @return
     */
    @PostMapping("/update")
    public JsonResult<Content> updateContent(@RequestBody Content content, HttpSession session){
        System.out.println(content);
        Integer modifiedUser = getUserIdFromSession(session);
        String modifyType = getUserTypeFromSession(session);
        content.setCreatorType(modifyType);
        contentService.updateContent(content, modifiedUser);
        contentService.contentCompletionUpdate(0,content.getId(),modifiedUser);
        Content c = contentService.findById(content.getId());
        return new JsonResult<>(OK,c);
    }

    /**
     * 用于封面图片上传 替换  需要前置校验 案例是否存在
     * @param file
     * @param contentId 案例是不存在 则为错误的请求
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping("/add_cover/{contentId}")
    public JsonResult<Resource> contentAddCover(MultipartFile file, @PathVariable Integer contentId, HttpSession session) throws IOException {
        Integer modifiedUser = getUserIdFromSession(session);
        Resource resource = resourceService.UploadImage(file,modifiedUser);
        contentService.contentUpdateCover(contentId,resource,modifiedUser);
        return new JsonResult<>(OK, resource);
    }

    /**
     * 用于 点击 下一步 前端主动设置任务完成状态
     * @param contentId
     * @param completion 当前进行的步骤
     * @param session
     * @return
     */
    @PostMapping("/update_completion/{contentId}")
    public JsonResult<Content> contentCompletionUpdate(@PathVariable Integer contentId, Integer completion, HttpSession session){
        Integer modifiedUser = getUserIdFromSession(session);
        contentService.contentCompletionUpdate(completion,contentId,modifiedUser);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK,content);
    }

    /**
     * 第三步
     * @param contentId
     * @param models
     * @param session
     * @return
     */
    @PostMapping("/update_points/{contentId}")
    public JsonResult<Content> contentAddPoints(@PathVariable Integer contentId, @RequestBody List<Integer> models, HttpSession session){
        Integer modifiedUser = getUserIdFromSession(session);
        contentService.contentUpdatePoints(contentId,models,modifiedUser);
        // 完成了知识点的设计
        contentService.contentCompletionUpdate(2,contentId,modifiedUser);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK,content);
    }

    /**
     * 第四步
     * @param contentId
     * @param session
     * @return
     */
    @PostMapping("/update_design/{contentId}")
    public JsonResult<Content> contentAddedDesign(@PathVariable Integer contentId,HttpSession session){
        Integer modifiedUser = getUserIdFromSession(session);
        contentService.contentCompletionUpdate(3,contentId,modifiedUser);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK,content);
    }

    /**
     * 第五步
     * @param contentId
     * @param session
     * @return
     */
    // 任务模版在taskTemplate
    @PostMapping("/update_task_template/{contentId}")
    public JsonResult<Content> contentAddedTaskTemplate(@PathVariable Integer contentId,HttpSession session){
        Integer modifiedUser = getUserIdFromSession(session);
        contentService.contentCompletionUpdate(4,contentId,modifiedUser);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK,content);
    }

    /**
     * 第六步
     * @param data
     * @param contentId
     * @param session
     * @return
     */
    @PostMapping("/update_guidance/{contentId}")
    public JsonResult<Content> updateGuidance(@RequestBody Content data, @PathVariable Integer contentId, HttpSession session){
        String guidance = data.getGuidance();
        System.out.println(guidance);
        Integer user = getUserIdFromSession(session);
        contentService.updateGuidanceById(contentId, guidance, user);
        contentService.contentCompletionUpdate(5,contentId,user);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK,content);
    }

    /**
     * 第七步
     * 用于上传单个资源包
     * @return
     */
    @PostMapping("/upload_pkg/{contentId}")
    public JsonResult<List> uploadContentPkg(MultipartFile file, @PathVariable Integer contentId, HttpSession session)throws IOException{
        Integer creator = getUserIdFromSession(session);
        Resource resource = resourceService.UploadFile(file,creator);
        contentService.contentAddPkg(contentId,resource);
        List<ResourceVo> resources = contentService.findResourcesById(contentId);
        return new JsonResult<>(OK, resources);
    }

    /**
     * 第七步删除案例的参考资料
     * @param contentId
     * @param pkgId
     * @return
     */
    @GetMapping("/delete_pkg/{contentId}/{pkgId}")
    public JsonResult<List> deleteContentPkg(@PathVariable Integer contentId, @PathVariable Integer pkgId){
        contentService.contentDeletePkg(contentId,pkgId);
        List<ResourceVo> resources = contentService.findResourcesById(contentId);
        return new JsonResult<>(OK,resources);
    }

    /**
     * 项目内容填充完成
     * @param contentId
     * @param session
     * @return
     */
    @GetMapping("/done/{contentId}")
    public JsonResult<Content> setContentDone(@PathVariable Integer contentId, HttpSession session){
        Integer modifiedUser = getUserIdFromSession(session);
        contentService.contentCompletionUpdate(6,contentId,modifiedUser);
        Content content = contentService.findById(contentId);
        return new JsonResult<>(OK, content);
    }

    @DeleteMapping("/delete")
    public JsonResult<Void> deleteContent(Integer id, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        contentService.deleteContent(id, lastModifiedUser);
        return  new JsonResult<>(OK);
    }

    /**
     * 教师查询自己创建的项目
     * @param session
     * @return
     */
    @GetMapping("/teacher_creat")
    public JsonResult<List> getTeacherCreate(HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<Content> contents = contentService.getTeacherCreate(teacherId);
        return new JsonResult<>(OK, contents);
    }

    @GetMapping("/admin_create")
    public JsonResult<List> getAdminCreate(HttpSession session){
        Integer adminId = getUserIdFromSession(session);
        List<Content> contents = contentService.getAdminCreate(adminId);
        return new JsonResult<>(OK, contents);
    }

    @GetMapping("/need_check")
    public JsonResult<List> needCheck(HttpSession session){
        List<Content> contents = contentService.needCheck();
        return new JsonResult<>(OK, contents);
    }

    @GetMapping("/check")
    public JsonResult<Void> check(Integer contentId, HttpSession session){
        Integer adminId = getUserIdFromSession(session);
        contentService.check(contentId,adminId);
        return new JsonResult<>(OK);
    }


    @GetMapping("/by_id")
    public JsonResult<Content> findByID(Integer id){
        Content content = contentService.findById(id);
        return new JsonResult<>(OK, content);
    }

    @GetMapping("/guidance")
    public JsonResult<String> guidance(Integer id){
        String guidance = contentService.findGuidanceById(id);
        return new JsonResult<>(OK, guidance);
    }


    @GetMapping("/packages")
    public JsonResult<List> packages(Integer id){
        List<ResourceVo> files = contentService.findResourcesById(id);
        return new JsonResult<>(OK, files);
    }

    @GetMapping("/all_classified")
    public JsonResult<List> classifiedAll(Integer parentId){
        List<Content> contents = contentService.findByParentId(parentId);
        return new JsonResult<>(OK,contents);
    }

    @GetMapping("/classified")
    public JsonResult<List> classified(Integer parentId){
        List<Content> contents = contentService.findNotDelByParentId(parentId);
        return new JsonResult<>(OK,contents);
    }

    /**
     * 案例删除后恢复
     * @param id
     * @param session
     * @return
     */
    @GetMapping("/restore")
    public JsonResult<Void> restore(Integer id, HttpSession session){
        Integer lastModifiedUser = getUserIdFromSession(session);
        contentService.restore(id, lastModifiedUser);
        return new JsonResult<>(OK);
    }

    /**
     * 所有案例
     * @return
     */
    @GetMapping("/all")
    public JsonResult<List> allContent(){
        List<Content> contents = contentService.findAll();
        return new JsonResult<>(OK, contents);
    }

    /**
     * 精选案例
     * @param session
     * @param id
     * @return
     */
    // 待补充

    @GetMapping("/get_by_t_id")
    public JsonResult<List> findByTeacherId(HttpSession session, Integer id){
        Integer teacherId = getUserIdFromSession(session);
        if(id != null){
            teacherId = id;
        }
        List<Content> contents = contentService.findByTeacherId(teacherId);
        return new JsonResult<>(OK,contents);
    }

    @GetMapping("/tasks")
    public JsonResult<List> findTaskTemplatesByContentId(Integer contentId){
        List<TaskTemplateDto> taskTemplates = contentService.findTaskTemplatesByContentId(contentId);
        return new JsonResult<>(OK, taskTemplates);
    }

    /**
     * 授权
     * @param caseAccreditQo
     * @return
     */
    @PostMapping("/case_accredit")
    public JsonResult<Void> CaseAccredit(@RequestBody CaseAccreditQo caseAccreditQo){
        contentService.CaseAccredit(caseAccreditQo.getTeacherId(), caseAccreditQo.getContentIds());
        return new JsonResult<>(OK);
    }
}
