package com.iecube.community.model.project.mapper;

import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectStudent;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.student.entity.StudentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
    Integer insert(Project project);
    Project findById(Integer id);

    Project findByPstId(Integer pstId);

    Integer findCaseTypeByCaseId(Integer caseId);

    Integer delete(Integer id);

    Integer hidden(Integer id);

    Integer disHidden(Integer id);

    Integer addProjectStudent(ProjectStudent pStudent);
    List<Project> findByCreator(Integer teacherId);
    List<Project> findByCreatorNotDel(Integer teacherId);
    List<ProjectStudentVo> findStudentsByProjectId(Integer projectId); // 没有pstId
    List<ProjectStudentVo> findStudentsPSTByProjectId(Integer projectId); // 有pstId

    List<Integer> findStudentIdByProjectId(Integer projectId);
    List<Project> findByStudentId(Integer id);

    List<Project> findCourseByStudentId(Integer id);

    Integer studentNumOfCurrentProject(Integer projectId);

    List<Project> findByCaseId(Integer caseId);

    ProjectStudentVo findProjectStudent(Integer projectId, Integer studentId);

    Integer updateProjectStudentGrade(Integer id, Double grade);

    List<StudentDto> getProjectStudents(Integer projectId);

    List<Project> findStuEMDV4Course(Integer studentId);

    void publishProjectGrade(Integer projectId);
    void cancelPublishProjectGrade(Integer projectId);
}
