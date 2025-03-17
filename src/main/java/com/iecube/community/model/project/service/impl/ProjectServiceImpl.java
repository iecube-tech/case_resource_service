package com.iecube.community.model.project.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.markdown.entity.MDArticle;
import com.iecube.community.model.markdown.service.MarkdownService;
import com.iecube.community.model.project.service.ex.GenerateFileException;
import com.iecube.community.model.project.service.ex.StudentAlreadyInProject;
import com.iecube.community.model.pst_article.entity.PSTArticle;
import com.iecube.community.model.pst_article.service.PSTArticleService;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.remote_project.entity.RemoteProject;
import com.iecube.community.model.remote_project.qo.RemoteProjectQo;
import com.iecube.community.model.remote_project.service.RemoteProjectService;
import com.iecube.community.model.remote_project_join_device.entity.RemoteProjectDevice;
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
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
import com.iecube.community.util.DeleteFolderUtils;
import com.iecube.community.util.TimeFormat;
import com.iecube.community.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Value("${export-report}")
    private String reportPath;

    @Value("${export-grade}")
    private String exportGradePath;

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
    private TaskTemplateService taskTemplateService;

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

    @Autowired
    private RemoteProjectService remoteProjectService;

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private PSTArticleService pstArticleService;

    @Autowired
    private EMDTaskService emdTaskService;


    SimpleDateFormat dateFormat =new SimpleDateFormat("YYYY-MM-dd");

    @Override
    public Integer addProject(ProjectDto projectDto, Integer teacherId) {
        // todo 数据校验 必须要包含的数据
//        先判断创建的是课程还是案例
//        更改案例名称  如果和原来的案例不同  则不动   如果不同，则添加时间后缀
//        this.checkProject(projectDto, teacherId);
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
        project.setDeviceId(content.getDeviceId());
        project.setUseGroup(projectDto.getUseGroup());
        project.setGroupLimit(projectDto.getGroupLimit());
        project.setMdCourse(content.getMdCourse()); // 是否使用markdown的形式
        project.setUseRemote(projectDto.getUseRemote());
        project.setEmdCourse(content.getEmdCourse());
        Integer row = projectMapper.insert(project);
        if (row != 1){
            throw new InsertException("插入数据异常");
        }
        // 添加项目的学生
        for(Student student: projectDto.getStudents()){
            ProjectStudent pStudent = new ProjectStudent();
            pStudent.setProjectId(project.getId());
            pStudent.setStudentId(student.getId());
            pStudent.setCreator(teacherId);
            pStudent.setCreateTime(new Date());
            Integer row2 = projectMapper.addProjectStudent(pStudent);
            if (row2!=1){
                throw new InsertException("插入数据异常");
            }
        }
        Integer projectId = project.getId();
        if(project.getEmdCourse()!= null){
            this.createEMDTask(projectDto, project, teacherId);
        }else{
            this.createRoutineTask(projectDto, project, teacherId);
        }
        return projectId;
    }

    private void createRoutineTask(ProjectDto projectDto, Project project, Integer teacherId) {
        // 开启远程实验
        if(Objects.equals(project.getUseRemote(), 1)){
            // 检查数据是否合理
            RemoteQo remoteQo = projectDto.getRemoteQo();
            if(remoteQo.getRemoteDeviceIdList().isEmpty()){
                projectMapper.delete(project.getId());
                throw new InsertException("没有添加远程设备");
            }
            if(remoteQo.getEndTime() == null || remoteQo.getStartTime()== null || remoteQo.getStartDate()== null || remoteQo.getEndDate()== null){
                projectMapper.delete(project.getId());
                throw new InsertException("没有添加远程实验时间信息");
            }
            if(remoteQo.getAppointmentCount() == null|| remoteQo.getAppointmentDuration() == null){
                projectMapper.delete(project.getId());
                throw new InsertException("没有添加远程实验学生限制");
            }
            // 定义日期时间格式化器，根据日期字符串的格式定义
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

            LocalDate startLocalDate = LocalDate.parse(remoteQo.getStartDate(), formatterDate);
            LocalDate endLocalDate = LocalDate.parse(remoteQo.getEndDate(), formatterDate);
            LocalTime startLocalTime = LocalTime.parse(remoteQo.getStartTime(), formatterTime);
            LocalTime endLocalTime = LocalTime.parse(remoteQo.getEndTime(), formatterTime);
            RemoteProject remoteProject = new RemoteProject();
            remoteProject.setProjectId(project.getId());
            remoteProject.setStartDate(startLocalDate);
            remoteProject.setEndDate(endLocalDate);
            remoteProject.setStartTime(startLocalTime);
            remoteProject.setEndTime(endLocalTime);
            remoteProject.setAppointmentCount(remoteQo.getAppointmentCount());
            remoteProject.setAppointmentDuration(remoteQo.getAppointmentDuration());
            remoteProject.setDayLimit(remoteQo.getDayLimit());
            List<RemoteProjectDevice> remoteDeviceList = new ArrayList<>();
            remoteQo.getRemoteDeviceIdList().forEach(item -> {
                RemoteProjectDevice remoteProjectDevice = new RemoteProjectDevice();
                remoteProjectDevice.setDeviceId(item);
                remoteProjectDevice.setProjectId(project.getId());
                remoteDeviceList.add(remoteProjectDevice);
            });
            RemoteProjectQo remoteProjectQo = new RemoteProjectQo();
            remoteProjectQo.setRemoteProjectDeviceList(remoteDeviceList);
            remoteProjectQo.setRemoteProject(remoteProject);

            remoteProjectService.addRemoteProject(remoteProjectQo);
        }

        // 有了项目之后创建项目的任务
        List<PSTArticle> willAddPSTArticle = new ArrayList<>();
        for(Task task: projectDto.getTask()){
            task.setProjectId(project.getId());
            // 创建项目任务  返回一个创建好的taskId
            Task createdTask = taskService.createTask(task, teacherId);
            // 项目学生任务关联
            for(Student student: projectDto.getStudents()){
                ProjectStudentTask PST = new ProjectStudentTask();
                PST.setProjectId(project.getId());
                PST.setTaskId(createdTask.getId());
                PST.setStudentId(student.getId());
                PST.setStatus(0);
                if (task.getNum()==1) {
                    PST.setStatus(1);
                }
                Integer res = taskMapper.addStudentTask(PST);
                if(createdTask.getTaskMdDoc()!=null){
                    // 生成pst的markdownOperate
                    MDArticle mdArticle = markdownService.getFullArticleByChapter(createdTask.getTaskMdDoc());
                    // 复制原文档到每个pst
                    List<PSTArticleCompose> pstArticleComposeList = new ArrayList<>();
                    mdArticle.getComposeList().forEach(mdArticleCompose -> {
                        PSTArticleCompose pstArticleCompose = new PSTArticleCompose();
                        pstArticleCompose.setPstId(PST.getId());
                        pstArticleCompose.setName(mdArticleCompose.getName());
                        pstArticleCompose.setIndex(mdArticleCompose.getIndex());
                        pstArticleCompose.setVal(mdArticleCompose.getVal());
                        pstArticleCompose.setAnswer(mdArticleCompose.getAnswer());
                        pstArticleCompose.setScore(mdArticleCompose.getScore());
                        pstArticleCompose.setSubjective(mdArticleCompose.getSubjective());
                        pstArticleCompose.setQType(mdArticleCompose.getQType());
                        pstArticleCompose.setQuestion(mdArticleCompose.getQuestion());
                        pstArticleCompose.setArgs(mdArticleCompose.getArgs());
                        pstArticleComposeList.add(pstArticleCompose);
                    });
                    PSTArticle pstArticle = new PSTArticle();
                    pstArticle.setPstId(PST.getId());
                    pstArticle.setContent(mdArticle.getContent());
                    pstArticle.setCatalogue(mdArticle.getCatalogue());
                    pstArticle.setComposeList(pstArticleComposeList);
                    willAddPSTArticle.add(pstArticle);
                }
            }
        }
        if(!willAddPSTArticle.isEmpty()){
            // 复制的内容存储
            pstArticleService.addedProject(willAddPSTArticle);
        }
        //创建项目tag
        tagService.tagTemplateToTag(projectDto.getCaseId(),project.getId(),teacherId);
    }

    private void createEMDTask(ProjectDto projectDto, Project project, Integer teacherId) {
        // projectDto 中 用户teacher携带的task ==> 创建task -> task表 &&  -> e_md_task_proc表
        List<Task> EMDTaskList = new ArrayList<>();
        for(Task task: projectDto.getTask()){
            task.setProjectId(project.getId());
            Task createdTask = taskService.createTask(task, teacherId);
            EMDTaskList.add(createdTask);
        }
        // todo EMD 课程发布流程
        emdTaskService.EMDTaskPublish(projectDto.getStudents(), EMDTaskList);
    }

    public Integer studentJoinProject(Integer projectId, Integer studentId){
        // 判断学生是不是已经在
        List<Project> StudentProjectList = projectMapper.findByStudentId(studentId);
        for(Project project:StudentProjectList){
            if(Objects.equals(projectId, project.getId())){
                throw new StudentAlreadyInProject("已在其中");
            }
        }
        Student student = studentMapper.getStudentById(studentId);
        ProjectStudent pStudent = new ProjectStudent();
        pStudent.setProjectId(projectId);
        pStudent.setStudentId(student.getId());
        pStudent.setCreator(student.getId());
        pStudent.setCreateTime(new Date());
        Integer row2 = projectMapper.addProjectStudent(pStudent);
        if (row2!=1){
            throw new InsertException("插入数据异常");
        }

        List<TaskVo> projectTasks =taskService.studentGetProjectTasks(projectId);
        List<PSTArticle> willAddPSTArticle = new ArrayList<>();
        for(TaskVo task : projectTasks){
            ProjectStudentTask PST = new ProjectStudentTask();
            PST.setProjectId(projectId);
            PST.setTaskId(task.getId());
            PST.setStudentId(student.getId());
            PST.setStatus(0);
            if (task.getNum()==1) {
                PST.setStatus(1);
            }
            Integer co = taskMapper.addStudentTask(PST);
            if(co!=1){
                throw new InsertException("插入数据异常");
            }
            if(task.getTaskMdDoc()!=null){
                // 生成pst的markdownOperate
                MDArticle mdArticle = markdownService.getFullArticleByChapter(task.getTaskMdDoc());
                List<PSTArticleCompose> pstArticleComposeList = new ArrayList<>();
                mdArticle.getComposeList().forEach(mdArticleCompose -> {
                    PSTArticleCompose pstArticleCompose = new PSTArticleCompose();
                    pstArticleCompose.setPstId(PST.getId());
                    pstArticleCompose.setName(mdArticleCompose.getName());
                    pstArticleCompose.setIndex(mdArticleCompose.getIndex());
                    pstArticleCompose.setVal(mdArticleCompose.getVal());
                    pstArticleCompose.setAnswer(mdArticleCompose.getAnswer());
                    pstArticleCompose.setScore(mdArticleCompose.getScore());
                    pstArticleCompose.setSubjective(mdArticleCompose.getSubjective());
                    pstArticleCompose.setQType(mdArticleCompose.getQType());
                    pstArticleCompose.setQuestion(mdArticleCompose.getQuestion());
                    pstArticleCompose.setArgs(mdArticleCompose.getArgs());
                    pstArticleComposeList.add(pstArticleCompose);
                });
                PSTArticle pstArticle = new PSTArticle();
                pstArticle.setPstId(PST.getId());
                pstArticle.setContent(mdArticle.getContent());
                pstArticle.setCatalogue(mdArticle.getCatalogue());
                pstArticle.setComposeList(pstArticleComposeList);
                willAddPSTArticle.add(pstArticle);
            }
        }
        if(!willAddPSTArticle.isEmpty()){
            pstArticleService.addedProject(willAddPSTArticle);
        }
        return projectId;
    }

    @Override
    public List<StudentDto> getProjectStudents(Integer projectId) {
        List<StudentDto> studentDtoList = projectMapper.getProjectStudents(projectId);
        return studentDtoList;
    }

    /**
     * 新增需求， 如果创建project过程中 教师更改了案例的任务列表，那么在后期数据统计分析的过程中该project的数据为无效数据，
     * 影响整体数据的有效性。为避免该问题，每当有更改原案例的任务的project创建，就从该案例衍生出一个新的案例，为该教师所有
     * @param projectDto
     * @param teacherId
     */
    private void checkProject(ProjectDto projectDto, Integer teacherId){
        List<Task> newTasks = projectDto.getTask();
        List<TaskTemplateDto> oldTask = taskTemplateService.findTaskTemplateByContent(projectDto.getCaseId());
//        System.out.println(newTasks);
//        System.out.println(oldTask);
        if(newTasks.size()!=oldTask.size()){
            //new case
//            System.out.println("new case");
            return;
        }
        for(int i=0; i<newTasks.size(); i++){
            if(newTasks.get(i).getRequirementList().size()!=oldTask.get(i).getRequirementList().size()){
                //new case
//                System.out.println("new case");
                return;
            }
            for(int j=0; j<newTasks.get(i).getRequirementList().size();j++){
                if(newTasks.get(i).getRequirementList().get(j).getName() != oldTask.get(i).getRequirementList().get(j).getName()){
                    //new case
//                    System.out.println("new case");
                    return;
                }
            }
            if(newTasks.get(i).getDeliverableRequirementList().size()!=oldTask.get(i).getDeliverableRequirementList().size()){
                //new case
//                System.out.println("new case");
                return;
            }
            for(int j=0; j<newTasks.get(i).getDeliverableRequirementList().size(); i++){
                if(newTasks.get(i).getDeliverableRequirementList().get(j).getName() != oldTask.get(i).getDeliverableRequirementList().get(j).getName()){
                    //new case
//                    System.out.println("new case");
                    return;
                }
            }
        }
//        return projectDto;
    }

    /**
     * 教师更改了案例的原有任务列表，穿件该案例的衍生案例。
     *  ****设计前预想产生新的问题， 当教师删除掉这个衍生案例后， 已经创建的project是否会有影响。 **重点测试**
     * @param oldCaseId
     * @param taskList
     */
    private void createNewCase(Integer oldCaseId, List<Task> taskList){
        //contentService实现
    }

    @Override
    public List<Project> myProject(Integer teacherId) {
        List<Project> myProjects = projectMapper.findByCreator(teacherId);
        return myProjects;
    }

    @Override
    public List<Project> myProjectNotDel(Integer teacherId) {
        List<Project> myProjects = projectMapper.findByCreatorNotDel(teacherId);
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

    @Override
    public List<Project> findCourseByStudentId(Integer id) {
        List<Project> courseList = projectMapper.findCourseByStudentId(id);
        return courseList;
    }

    public StudentProjectVo studentProjectDetail(Integer projectId){
        Project project = projectMapper.findById(projectId);
        StudentProjectVo studentProject = new StudentProjectVo();
        studentProject.setProject(project);
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
        Content content = contentService.findById(project.getCaseId());
        project.setFourth(content.getFourth());
        project.setFourthType(content.getFourthType());
        Integer caseType = projectMapper.findCaseTypeByCaseId(project.getCaseId());
        project.setCaseType(caseType);
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

    @Override
    public File exportProjectData(Integer projectId) {
        //判断有没有文件

        //有文件直接返回下载

        //没有文件生成文件返回下载
        return null;
    }
    @Override
    public File ReGenerateProjectData(Integer projectId){
        File file = this.GenerateProjectData(projectId);
        return file;
    }

    public File GenerateProjectData(Integer projectId){
        List<ProjectStudentVo> projectStudentVoList = this.projectStudentAndStudentTasks(projectId);
        List<TaskVo> projectTasks = taskService.getProjectTasks(projectId);
        Project project = this.findProjectById(projectId);
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("Sheet1");
        // 创建行，行号从0开始
        Row row = sheet.createRow(0);
        // 创建单元格，列号从0开始
        // 编号，姓名，学号，任务一，任务二， 任务三，任务四，任务五，项目成绩，tag点，改进建议
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("学号");
        for(int i=0;i<projectTasks.size();i++){
            row.createCell(3*i+3).setCellValue(projectTasks.get(i).getTaskName());
            row.createCell(3*i+4).setCellValue(projectTasks.get(i).getTaskName()+"-tag点");
            row.createCell(3*i+5).setCellValue(projectTasks.get(i).getTaskName()+"-改进建议");
        }
        row.createCell(projectTasks.size()*3+3).setCellValue("总成绩");

        for(int i=0; i<projectStudentVoList.size();i++){
            // 创建一行，并写入具体数据
            Row dataRow = sheet.createRow(i+1);
            dataRow.createCell(0).setCellValue(i+1);
            dataRow.createCell(1).setCellValue(projectStudentVoList.get(i).getStudentName());
            dataRow.createCell(2).setCellValue(projectStudentVoList.get(i).getStudentId());
            for(int j=0;j<projectStudentVoList.get(i).getStudentTasks().size();j++){
                if(projectStudentVoList.get(i).getStudentTasks().get(j).getTaskGrade() != null){
                    dataRow.createCell(3*j+3).setCellValue(projectStudentVoList.get(i).getStudentTasks().get(j).getTaskGrade());
                }
                List<Tag> tags = projectStudentVoList.get(i).getStudentTasks().get(j).getTags();
                StringBuilder tagBuilder = new StringBuilder();
                tagBuilder.append(" ");
                StringBuilder suggestionBuilder = new StringBuilder();
                suggestionBuilder.append(" ");
                for(int k=0;k<tags.size();k++){
                    tagBuilder.append(tags.get(k).getName());
                    tagBuilder.append(" ");
                    suggestionBuilder.append(tags.get(k).getSuggestion());
                    suggestionBuilder.append(" ");
                }
                dataRow.createCell(3*j+4).setCellValue(tagBuilder.toString());
                dataRow.createCell(3*j+5).setCellValue(suggestionBuilder.toString());
            }
            if(projectStudentVoList.get(i).getStudentGrade()!=null){
                dataRow.createCell(projectStudentVoList.get(i).getStudentTasks().size()*3+3).setCellValue(projectStudentVoList.get(i).getStudentGrade());
            }
        }
        try {
            String excelPath = exportGradePath+"/"+project.getProjectName()+TimeFormat.timeFormat(project.getStartTime())
                    +"-"+TimeFormat.timeFormat(project.getEndTime())+".xlsx";
            File oldFile = new File(excelPath);
            if(oldFile.exists()){
                oldFile.delete();
            }
            // 将工作簿写入文件
            FileOutputStream outputStream = new FileOutputStream(excelPath);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
//            System.out.println("");
            log.info(project.getProjectName()+"项目，id:"+projectId+"的导出文件:"+excelPath+"创建成功!");
            return new File(excelPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GenerateFileException("导出成绩创建文件失败");
        }
    }

    @Override
    public void deleteProject(Integer projectId) {
        //todo
        Integer row = projectMapper.delete(projectId);
        remoteProjectService.deleteRemoteProject(projectId);
        if(row!= 1){
            throw new UpdateException("数据更新异常");
        }
    }

    @Override
    public void hiddenProject(Integer projectId) {
        Project project = this.findProjectById(projectId);
        int row = 0;
        if(project.getHidden().equals(0)){
            row = projectMapper.hidden(projectId);
        }else {
            row = projectMapper.disHidden(projectId);
        }
        if(row!=1){
            throw new UpdateException("数据更新异常");
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
                        try{
                            FileCopyUtils.copy(source,target);
                            log.info("cp--"+source+"--to--"+target);
                        }catch (IOException e ){
                            log.error(e.getMessage());
                        }
                    }
                    if(taskResource.getReadOver()!=null){
                        File source = new File(files, taskResource.getReadOver().getFilename());
                        File target = new File(StudentReportPath.toString(), taskResource.getReadOver().getOriginFilename());
                        try{
                            FileCopyUtils.copy(source,target);
                            log.info("cp--"+source+"--to--"+target);
                        }catch (IOException e){
                            log.error(e.getMessage());
                        }
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
