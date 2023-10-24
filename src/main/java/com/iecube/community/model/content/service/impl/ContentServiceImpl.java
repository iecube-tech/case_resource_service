package com.iecube.community.model.content.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.entity.casePkg;
import com.iecube.community.model.content.entity.taskTemplates;
import com.iecube.community.model.content.mapper.ContentMapper;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.model.content.service.ex.ContentDidNotDel;
import com.iecube.community.model.content.service.ex.ContentNotFoundException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.npoints.mapper.NPointsMapper;
import com.iecube.community.model.npoints.service.NPointsService;
import com.iecube.community.model.npoints.vo.ModuleConceptVo;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.entity.ResourceVo;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.mapper.TaskTemplateMapper;
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
import com.iecube.community.model.teacher.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private TaskTemplateService taskTemplateService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private NPointsService nPointsService;

    @Autowired
    private NPointsMapper nPointsMapper;

    @Override
    public Integer addContent(Content content, String userType, Integer lastModifiedUser) {
        content.setParentId(1);
        content.setCompletion(0);
        content.setIsDelete(0);
        content.setCreatorType(userType);
        content.setCreator(lastModifiedUser);
        content.setCreateTime(new Date());
        content.setLastModifiedUser(lastModifiedUser);
        content.setLastModifiedTime(new Date());
        /**判断权限， 是否有添加内容的权限**/
        /**处理数据**/
        /**添加数据**/
        Integer rows = contentMapper.insert(content);
        if (rows != 1){
            throw new InsertException("插入数据异常");
        }
        return content.getId();
    }

    @Override
    public void contentUpdateCover(Integer contentId, Resource resource, Integer lastModifiedUser) {
        Content content = this.findById(contentId);
        if(content.getCover() != null && !content.getCover().isEmpty()){
            // 删除原先的cover
            resourceService.deleteResource(content.getCover());
        }
        Integer row = contentMapper.updateCover(contentId, resource.getFilename(),lastModifiedUser, new Date());
        if(row!=1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void updateContent(Content content, Integer lastModifiedUser) {
        content.setLastModifiedTime(new Date());
        content.setLastModifiedUser(lastModifiedUser);
        /**判断是否存在**/
        Content oldContent = contentMapper.findById(content.getId());
        if (oldContent == null || oldContent.getIsDelete()==1){
            throw new ContentNotFoundException("未找到该内容");
        }
        /**判断是否有权限修改**/

        /**处理数据**/

        /**更新数据**/
        Integer rows = contentMapper.update(content);
        if (rows != 1){
            throw new UpdateException("更新数据异常");
        }

    }

    @Override
    public void deleteContent(Integer id, Integer lastModifiedUser) {
        /**判断数据是否存在**/
        Content oldContent = contentMapper.findById(id);
        if (oldContent == null || oldContent.getIsDelete() == 1){
            throw new ContentNotFoundException("未找到该数据");
        }
        /**判断是否有权限删除**/

        /**删除数据**/
        Integer rows = contentMapper.delete(id, lastModifiedUser, new Date());
        if (rows != 1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void contentCompletionUpdate(Integer completion, Integer contentId, Integer lastModifiedUser) {
        Integer row = contentMapper.contentCompletionUpdate(contentId,completion,lastModifiedUser, new Date());
        if(row!=1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void contentUpdatePoints(Integer id, List<Integer> modules, Integer lastModifiedUser) {
        List<ModuleConceptVo> moduleConceptVoList = nPointsService.getModulesByCase(id);
        List<Integer> oldModules = new ArrayList<>();
        for(int i=0;i<moduleConceptVoList.size(); i++){
            oldModules.add(moduleConceptVoList.get(i).getId());
        }
        List<Integer> Subtract = new ArrayList<>();
        Subtract.addAll(oldModules);
        //删除==> 原先的列表 去掉和新列表中的相同元素  剩余的就是要删除的
        Subtract.removeAll(modules);
        for(Integer i:Subtract){
            nPointsMapper.deleteCaseModule(id,i);
        }
        //新增==> 新列表去掉和旧列表相同的元素 剩余的就是要增加的
        modules.removeAll(oldModules);
        for(Integer i: modules){
            nPointsMapper.addCaseModule(id,i);
        }
    }

    @Override
    public void contentAddPkg(Integer id, Resource resource) {
        contentMapper.caseAddPkg(id, resource.getId());
    }

    @Override
    public void contentDeletePkg(Integer id, Integer pkgId) {
        resourceService.deleteById(pkgId);
        contentMapper.contentDeletePkg(id, pkgId);
    }

    @Override
    public List<Content> getTeacherCreate(Integer teacherId) {
        List<Content> contents = contentMapper.getTeacherCreate(teacherId);
        return contents;
    }

    @Override
    public List<Content> getAdminCreate(Integer adminId) {
        List<Content> contents = contentMapper.getAdminCreate(adminId);
        return contents;
    }

    @Override
    public List<Content> needCheck() {
        List<Content> contents = contentMapper.needCheck();
        return contents;
    }

    @Override
    public void check(Integer id, Integer adminId) {
        contentMapper.check(id,adminId,new Date());
    }

    @Override
    public Content findById(Integer id) {
        Content content = contentMapper.findById(id);
        content.setGuidance(null);
        if (content == null || content.getIsDelete() == 1){
            throw new ContentNotFoundException("未找到该数据");
        }
        return content;
    }

    @Override
    public String findGuidanceById(Integer id) {
        String Guidance = contentMapper.findGuidanceById(id);
        return Guidance;
    }

    @Override
    public void updateGuidanceById(Integer id, String guidance, Integer lastModifiedUser) {
        Integer row = contentMapper.updateGuidance(id, guidance, lastModifiedUser,new Date());
        if (row != 1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public List<ResourceVo> findResourcesById(Integer id) {
        List<ResourceVo> files = contentMapper.findResourcesById(id);
        return files;
    }

    /**查找一个类别下所有的**/
    @Override
    public List<Content> findByParentId(Integer parentId) {
        List<Content> contents = contentMapper.findByParentId(parentId);
        if (contents.size()==0){
            throw new ContentNotFoundException("未找到数据");
        }
        return contents;
    }

    /**查找一个列表下未删除的**/
    public List<Content> findNotDelByParentId(Integer parentId){
        List<Content> contents = contentMapper.findNotDelByParentId(parentId);
        if (contents.size()==0){
            throw new ContentNotFoundException("未找到数据");
        }
        return contents;
    }

    @Override
    public void restore(Integer id, Integer latModifiedUser) {
        /**判断是否有权限**/
        /**判断是否存在并且是已经删除的**/
        Content oldContent = contentMapper.findById(id);
        if(oldContent==null){
            throw new ContentNotFoundException("未找到该数据");
        }
        if (oldContent.getIsDelete()==0){
            throw new ContentDidNotDel("该数据未删除，无需回复");
        }
        Integer rows = contentMapper.restore(id, latModifiedUser, new Date());
        if (rows != 1){
            throw new UpdateException("数据更新异常");
        }
    }

    @Override
    public List<Content> findAll() {
        List<Content> contents = contentMapper.findAll();
        if (contents.size()==0){
            throw new ContentNotFoundException("未找到数据");
        }
        return contents;
    }

    @Override
    public List<Content> findByTeacherId(Integer teacherId) {
        List<Content> contents = contentMapper.findByTeacherId(teacherId);
        List<Content> teacherCreate = contentMapper.getTeacherCreate(teacherId);
        for(Content c:teacherCreate){
            if(c.getCompletion()<6){
                teacherCreate.remove(c);
            }
        }
        contents.addAll(teacherCreate);
        if (contents.size()==0){
            throw new ContentNotFoundException("您还没有可用案例");
        }
        return contents;
    }

    @Override
    public List<TaskTemplateDto> findTaskTemplatesByContentId(@RequestBody Integer contentId) {
//        List<taskTemplates> taskTemplates = contentMapper.findTaskTemplatesByContentId(contentId);
//        if(taskTemplates.size()==0){
//            throw new ContentNotFoundException("未找到数据");
//        }
//        return taskTemplates;
        List<TaskTemplateDto> taskTemplates = taskTemplateService.findTaskTemplateByContent(contentId);
        if(taskTemplates.size()==0){
            throw new ContentNotFoundException("未找到数据");
        }
        return taskTemplates;
    }

    @Override
    public void CaseAccredit(Integer teacherId, List<Integer> contentIds) {
        List<Integer> oldCases = contentMapper.findCaseIdsByTeacherId(teacherId);
        List<Integer> Subtract = new ArrayList<>();
        Subtract.addAll(oldCases);
        Subtract.removeAll(contentIds);
        System.out.println("old" + oldCases);
        System.out.println("cur" +contentIds);
        System.out.println("sub" + Subtract);
        contentIds.removeAll(oldCases);
        System.out.println("Add" + contentIds);
        for(Integer i :Subtract){
            contentMapper.teacherSubtractContent(teacherId,i);
        }
        for(Integer i:contentIds){
            contentMapper.teacherAddContent(teacherId,i);
        }
    }

}
