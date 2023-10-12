package com.iecube.community.model.project.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.tag.service.TagService;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.content.service.ContentService;
import com.iecube.community.model.project.entity.*;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.model.project.service.ex.ProjectNotFoundException;
import com.iecube.community.model.pst_resource.entity.PSTResource;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.pst_resource.mapper.PSTResourceMapper;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.mapper.TagMapper;
import com.iecube.community.model.task.entity.ProjectStudentTask;
import com.iecube.community.model.task.entity.StudentTaskVo;
import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.task.entity.TaskVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.DeleteFolderUtils;
import com.iecube.community.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Value("${export-report}")
    private String reportPath;

    @Value("${resource-location}/file")
    private String files;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ContentService contentService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PSTResourceMapper pstResourceMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private TagService tagService;


    SimpleDateFormat dateFormat =new SimpleDateFormat("YYYY-MM-dd");

    @Override
    public Integer addProject(ProjectDto projectDto, Integer teacherId) {
        Content content = contentService.findById(projectDto.getCaseId());
        // 创建项目
        Project project = new Project();
        project.setCaseId(projectDto.getCaseId());
        project.setProjectName(projectDto.getProjectName());
        project.setIntroduction(content.getIntroduction());
        project.setIntroduce(content.getIntroduce());
        project.setCover(content.getCover());
        project.setTarget(content.getTarget());
        project.setStartTime(projectDto.getDate().get(0));
        project.setEndTime(projectDto.getDate().get(1));
        project.setCreator(teacherId);
        project.setCreateTime(new Date());
        Integer row = projectMapper.insert(project);
        if (row != 1){
            throw new InsertException("插入数据异常");
        }
        Integer projectId = project.getId();
        // 添加项目的学生
        for(Student student: projectDto.getStudents()){
            ProjectStudent pStudent = new ProjectStudent();
            pStudent.setProjectId(projectId);
            pStudent.setStudentId(student.getId());
            pStudent.setCreator(teacherId);
            pStudent.setCreateTime(new Date());
            Integer row2 = projectMapper.addProjectStudent(pStudent);
            if (row2!=1){
                throw new InsertException("插入数据异常");
            }
        }
        // 有了项目之后创建项目的任务
        for(Task task: projectDto.getTask()){
            task.setProjectId(projectId);
            // 创建项目任务  返回一个创建好的taskId
            Integer pTaskId = taskService.createTask(task, teacherId);
            // 项目学生任务关联
            for(Student student: projectDto.getStudents()){
                ProjectStudentTask PST = new ProjectStudentTask();
                PST.setProjectId(projectId);
                PST.setTaskId(pTaskId);
                PST.setStudentId(student.getId());
                PST.setStatus(0);
                if (task.getNum()==1) {
                    PST.setStatus(1);
                }
                taskMapper.addStudentTask(PST);
            }
        }
        //创建项目tag
        tagService.tagTemplateToTag(projectDto.getCaseId(),projectId,teacherId);
        return projectId;
    }

    @Override
    public List<Project> myProject(Integer teacherId) {
        List<Project> myProjects = projectMapper.findByCreator(teacherId);
        return myProjects;
    }

    /**
     *  运行测试类查看返回的数据结构
     *  ProjectStudentVo(id=35, studentId=22408070201, studentName=黄小雯, studentTasks=[StudentTaskVo(PSTId=171, taskNum=1, taskGrade=null, taskStatus=null), StudentTaskVo(PSTId=203, taskNum=2, taskGrade=null, taskStatus=null), StudentTaskVo(PSTId=235, taskNum=3, taskGrade=null, taskStatus=null), StudentTaskVo(PSTId=267, taskNum=4, taskGrade=null, taskStatus=null), StudentTaskVo(PSTId=299, taskNum=5, taskGrade=null, taskStatus=null)])
     * @param projectId
     * @return
     */
    @Override
    public List<ProjectStudentVo> projectStudentAndStudentTasks(Integer projectId) {
        //根据项目id 查询该项目的所有学生
        List<ProjectStudentVo> projectStudents = projectMapper.findStudentsByProjectId(projectId);
        for (ProjectStudentVo student : projectStudents){
            //通过项目id和学生id查询到学生的该项目的所有任务简要信息
            List<StudentTaskVo> tasks = taskMapper.findTaskByProjectStudent(projectId ,student.getId());
            //上面这一步查出来的结果中没有tag信息
            List<String> studentSuggestions = new ArrayList<>();
            for(StudentTaskVo task:tasks){
                List<Tag> tags = tagMapper.getTagsByPSTId(task.getPSTId());
                task.setTags(tags);
                for(Tag tag:tags){
                    studentSuggestions.add(tag.getSuggestion());
                }
            }
            student.setStudentTasks(tasks);
            student.setSuggestion(studentSuggestions);
        }
        return projectStudents;
    }

    @Override
    public List<Project> findProjectByStudentId(Integer id) {
        List<Project> projects = projectMapper.findByStudentId(id);
        if (projects==null){
            throw new ProjectNotFoundException("没有数据");
        }
        return projects;
    }

    public StudentProjectVo studentProjectDetail(Integer projectId){
        Project project = projectMapper.findById(projectId);
        StudentProjectVo studentProject = new StudentProjectVo();
        studentProject.setProjectName(project.getProjectName());
        studentProject.setProjectCover(project.getCover());
        studentProject.setProjectIntroduce(project.getIntroduce());
        studentProject.setProjectIntroduction(project.getIntroduction());
        studentProject.setProjectTarget(project.getTarget());
        List<TaskVo> tasks = taskService.studentGetProjectTasks(projectId);
        studentProject.setProjectTaskList(tasks);
        return studentProject;
    }

    @Override
    public Project findProjectById(Integer id) {
        Project project = projectMapper.findById(id);
        if(project == null){
            throw new ProjectNotFoundException("任务未找到");
        }
        return project;
    }

    @Override
    public File downloadStudentReport(Integer projectId, Integer studentId){
        Project project = projectMapper.findById(projectId);
        File studentReport = this.studentReportGenerate(project, studentId);
        return studentReport;
    }

    @Override
    public File downloadProjectReport(Integer projectId){
        Project project = projectMapper.findById(projectId);
        List<Integer> students = projectMapper.findStudentIdByProjectId(projectId);
        for(Integer studentId:students){
            this.studentReportGenerate(project, studentId);
        }
        String projectReportPath = reportPath+"/"+projectId+"/"+project.getProjectName();
        try {
            OutputStream outputStream = new FileOutputStream(projectReportPath+".zip");
            ZipUtils.toZip(projectReportPath,outputStream,true);
            log.info("zip down "+projectReportPath+".zip");
            return new File(projectReportPath+".zip");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public File studentReportGenerate(Project project, Integer studentId){
        StudentDto student = studentMapper.getById(studentId);
        try {
            //创建学生文件夹
            String sPath = "/"+project.getId()+"/"+project.getProjectName()+"/"+student.getStudentId()+student.getStudentName();
            Path StudentReportPath = Files.createDirectories(Paths.get(reportPath+sPath));
            List<StudentTaskVo> studentTasks = taskMapper.findTaskByProjectStudent(project.getId(),studentId);
            List<List<PSTResourceVo>> studentTasksResources= new ArrayList<>();
            for(StudentTaskVo studentTask : studentTasks){
                List<PSTResourceVo> taskResources = new ArrayList<>();
                List<PSTResource> pstResources = pstResourceMapper.getPSTResourcesByPSTId(studentTask.getPSTId());
                for(PSTResource pSTResource:pstResources){
                    PSTResourceVo pstResourceVo = new PSTResourceVo();
                    pstResourceVo.setId(pSTResource.getId());
                    pstResourceVo.setPstId(pSTResource.getPSTId());
                    if(pSTResource.getResourceId() != null){
                        Integer resourceId = pSTResource.getResourceId();
                        Resource resource = resourceMapper.getById(resourceId);
                        pstResourceVo.setResource(resource);
                    }
                    if(pSTResource.getReadOverResourceId() != null){
                        pstResourceVo.setReadOver(resourceMapper.getById(pSTResource.getReadOverResourceId()));
                    }
                    taskResources.add(pstResourceVo);
                }
                studentTasksResources.add(taskResources);
            }
            for(List<PSTResourceVo> taskResources :studentTasksResources){
                for(PSTResourceVo taskResource : taskResources){
                    if(taskResource.getResource()!=null){
                        //将文件复制到学生的文件夹中
                        File source = new File(files, taskResource.getResource().getFilename());
//                        System.out.println(source);
                        File target = new File(StudentReportPath.toString(), taskResource.getResource().getOriginFilename());
//                        System.out.println(target);
                        FileCopyUtils.copy(source,target);
                        log.info("cp--"+source+"--to--"+target);
                    }
                    if(taskResource.getReadOver()!=null){
                        File source = new File(files, taskResource.getReadOver().getFilename());
                        File target = new File(StudentReportPath.toString(), taskResource.getReadOver().getOriginFilename());
                        FileCopyUtils.copy(source,target);
                        log.info("cp--"+source+"--to--"+target);
                    }
                }
            }
            OutputStream outputStream = new FileOutputStream(reportPath+sPath+".zip");
            ZipUtils.toZip(StudentReportPath.toString(),outputStream,true );
            log.info("zip down "+reportPath+sPath+".zip");
            DeleteFolderUtils.deleteFile(new File(reportPath+sPath));
            return new File(reportPath+sPath+".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
