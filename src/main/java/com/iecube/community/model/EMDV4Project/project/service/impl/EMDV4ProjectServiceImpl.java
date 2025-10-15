package com.iecube.community.model.EMDV4Project.project.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.BookLab.service.BookLabService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.EMDV4ProjectStudentService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.vo.EMDV4ProjectStudentVo;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service.EMDV4ProjectTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.service.EMDV4ProjectDataResultService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.EMDV4Project.project.service.EMDV4ProjectService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EMDV4ProjectServiceImpl implements EMDV4ProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ContentService contentService;

    @Autowired
    private BookLabService bookLabService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EMDV4ProjectStudentService projectStudentService;

    @Autowired
    private EMDV4ProjectTaskService projectTaskService;

    @Autowired
    private EMDV4ProjectDataResultService projectDataResultService;

    @Autowired
    private EMDV4ProjectStudentTaskService projectStudentTaskService;

    @Autowired
    private StudentMapper studentMapper;



    @Override
    public Integer addProject(EMDV4ProjectQo emdv4ProjectQo, Integer teacherId) {
        // 1. 创建project
        Project project = emdv4ProjectQoToProject(emdv4ProjectQo, teacherId);
        int res = projectMapper.insert(project);
        if(res!=1){
            throw new InsertException("新增课程数据异常");
        }
        // 远程实验
        projectService.createRemote(project,emdv4ProjectQo.getRemoteQo());
        List<Integer> classGradeIdLis = emdv4ProjectQo.getGradeClassList().stream().filter(Objects::nonNull).map(ClassAndGrade::getId).collect(Collectors.toList());
        System.out.println("classGradeIdLis:"+classGradeIdLis);
        List<StudentDto> studentDtoList = studentMapper.findByGradeClassIdList(classGradeIdLis);
        // 2. 创建 EMDV4_projectTask(课程实验) 返回 List<EMDV4ProjectTask>
        List<EMDV4ProjectTask> projectTaskList = projectTaskService.projectTaskListCreate(project, emdv4ProjectQo, studentDtoList);

        // 获取 projectTaskList 对应的详细的BookLab内容
        List<BookLabCatalog> labList = new ArrayList<>();
        projectTaskList.forEach(projectTask -> {
            labList.add(bookLabService.wholeBookLabCatalogById(projectTask.getLabId()));
        });

        // 3. 创建 EMDV4_projectStudent(课程学生) 返回 List<EMDV4ProjectStudent>
        List<EMDV4ProjectStudent> projectStudentList = projectStudentService.createProjectStudents(project, studentDtoList);

        // 4. 创建 EMDV4_project_data_result(课程数据分析) // todo 先放着
        projectDataResultService.createEMDV4ProjectDataResultService(project.getId());

        // 5. 创建 EMDV4_project_studentTask
        List<Integer> tasksTagNum = projectStudentTaskService.createProjectStudentTask(projectTaskList,labList, projectStudentList);

        // 6.更细学生 projectStudent 的共有几个tag 共有几个实验
        int projectTotalTagsNum=0;
        for (Integer i : tasksTagNum) {
            projectTotalTagsNum = projectTotalTagsNum + i;
        }
        projectStudentService.updateProjectStudentTotalNum(project.getId(), tasksTagNum.size(), projectTotalTagsNum);

        // 7.更新 projectTask共有几个学生
        projectTaskService.updateProjectTaskStudentNum(project.getId(), projectStudentList.size());

        return project.getId();
    }

    @Override
    public List<Project> stuProject(Integer studentId) {
        return projectMapper.findStuEMDV4Course(studentId);
    }

    @Override
    public Project getProject(Integer projectId) {
        return projectService.findProjectById(projectId);
    }

    public List<EMDV4ProjectStudentVo> addStudentToProject(List<Integer> studentIds, Integer projectId) {
        // 查询Project
        Project project = projectService.findProjectById(projectId);
        // 查询ProjectTask
        List<EMDV4ProjectTask> projectTaskList = projectTaskService.getProjectTaskList(projectId);
            // 获取 projectTaskList 对应的详细的BookLab内容
        List<BookLabCatalog> labList = new ArrayList<>();
        projectTaskList.forEach(projectTask -> {
            labList.add(bookLabService.wholeBookLabCatalogById(projectTask.getLabId()));
        });

        // 创建ProjectStudent
        List<StudentDto> studentDtoList = studentMapper.findByIdList(studentIds);
        List<EMDV4ProjectStudent> projectStudentList = projectStudentService.createProjectStudents(project, studentDtoList);

        // 使用Stream流过滤并收集结果
        List<EMDV4ProjectStudent> stuResultList = projectStudentList.stream()
                .filter(student -> studentIds.contains(student.getStudentId()))
                .collect(Collectors.toList());

        // 创建projectStudentTask
        List<Integer> tasksTagNum = projectStudentTaskService.createProjectStudentTask(projectTaskList,labList, stuResultList);

        // 6.更新学生 projectStudent 的共有几个tag 共有几个实验
        int projectTotalTagsNum=0;
        for (Integer i : tasksTagNum) {
            projectTotalTagsNum = projectTotalTagsNum + i;
        }
        projectStudentService.updateProjectStudentTotalNum(project.getId(), tasksTagNum.size(), projectTotalTagsNum);

        List<EMDV4ProjectStudentVo> projectStudentVoList = projectStudentService.getProjectStudentListByProjectId(projectId);
        // 7.更新 projectTask共有几个学生
        projectTaskService.updateProjectTaskStudentNum(project.getId(), projectStudentVoList.size());
        return projectStudentVoList;
    }


    public Project getProjectByTask(Long taskId){
        return null;
    }

    private Project emdv4ProjectQoToProject(EMDV4ProjectQo emdv4ProjectQo, Integer teacherId) {
        Content content = contentService.findById(emdv4ProjectQo.getCaseId());
        Project project = new Project();
        project.setCaseId(emdv4ProjectQo.getCaseId());
        project.setProjectName(emdv4ProjectQo.getProjectName());
        project.setIntroduction(content.getIntroduction());
        project.setIntroduce(content.getIntroduce());
        project.setCover(content.getCover());
        project.setTarget(content.getTarget());
        project.setStartTime(emdv4ProjectQo.getDate().get(0));
        project.setEndTime(emdv4ProjectQo.getDate().get(1));
        project.setDeviceId(content.getDeviceId());
        project.setUseGroup(emdv4ProjectQo.getUseGroup());
        project.setGroupLimit(emdv4ProjectQo.getGroupLimit());
        project.setHidden(0);
        project.setUseRemote(emdv4ProjectQo.getUseRemote());
        project.setFourth(content.getFourth());
        project.setFourthType(content.getFourthType());
        project.setEmdCourse(content.getEmdCourse());
        project.setVersion(4);
        project.setSemester(emdv4ProjectQo.getSemester());
        project.setGradeClass(emdv4ProjectQo.getGradeClass());
        project.setCreator(teacherId);
        project.setCreateTime(new Date());
        return project;
    }
}
