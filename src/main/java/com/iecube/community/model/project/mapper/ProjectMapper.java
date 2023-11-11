package com.iecube.community.model.project.mapper;

import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectStudent;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
    Integer insert(Project project);
    Project findById(Integer id);

    Integer delete(Integer id);

    Integer hidden(Integer id);

    Integer addProjectStudent(ProjectStudent pStudent);
    List<Project> findByCreator(Integer teacherId);
    List<ProjectStudentVo> findStudentsByProjectId(Integer projectId);

    List<Integer> findStudentIdByProjectId(Integer projectId);
    List<Project> findByStudentId(Integer id);

    List<Project> findCourseByStudentId(Integer id);

    Integer studentNumOfCurrentProject(Integer projectId);

    List<Project> findByCaseId(Integer caseId);

    ProjectStudentVo findProjectStudent(Integer projectId, Integer studentId);

    Integer updateProjectStudentGrade(Integer id, Integer grade);
}
