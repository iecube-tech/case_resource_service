package com.iecube.community.model.content.mapper;

import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.entity.casePkg;
import com.iecube.community.model.content.entity.taskTemplates;
import com.iecube.community.model.resource.entity.ResourceVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface ContentMapper {
    Integer insert(Content content);

    Integer update(Content content);

    Integer updateCover(Integer id, String cover, Integer lastModifiedUser, Date lastModifiedTime);

    Integer updateFourth(Integer id, String fourth, Integer lastModifiedUser, Date lastModifiedTime);

    Integer updateGuidance(Integer id, String guidance, Integer lastModifiedUser, Date lastModifiedTime);

    Integer delete(Integer id, Integer lastModifiedUser, Date lastModifiedTime);

    Integer contentCompletionUpdate(Integer id ,Integer completion, Integer lastModifiedUser, Date lastModifiedTime);

    List<Content> getTeacherCreate(Integer teacherId);

    List<Content> getAdminCreate(Integer adminId);

    List<Content> needCheck();

    Integer check(Integer id, Integer lastModifiedUser, Date lastModifiedTime);

    Content findById(Integer id);
    String findGuidanceById(Integer id);
    List<ResourceVo> findResourcesById(Integer id);

    List<Content> findByParentId(Integer parentId);

    List<Content> findNotDelByParentId(Integer parentId);

    Integer restore(Integer id, Integer lastModifiedUser, Date lastModifiedTime);

    List<Content> findAll();

    List<Content> findByTeacherId(Integer teacherId);

    Content findLast();

    Integer caseAddPkg(Integer caseId, Integer pkgId);

    Integer contentDeletePkg(Integer caseId, Integer pkgId);

    Integer contentAddTaskTemplate(taskTemplates taskTemplates);

    List<taskTemplates> findTaskTemplatesByContentId(Integer contendId);

    List<Integer> findCaseIdsByTeacherId(Integer teacherId);

    Integer teacherAddContent(Integer teacherId, Integer caseId);

    Integer teacherSubtractContent(Integer teacherId, Integer caseId);

    List<Content> allCourse();

    List<Content> teacherCourse(Integer teacherId);

    List<Content> teacherCreateCourseList(Integer creator);
}
