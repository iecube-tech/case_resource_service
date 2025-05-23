package com.iecube.community.model.task.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProcRef;
import com.iecube.community.model.elaborate_md.lab_proc.mapper.LabProcMapper;
import com.iecube.community.model.elaborate_md.lab_proc.mapper.LabProcRefMapper;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.service.MarkdownService;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.task_student_group.entity.Group;
import com.iecube.community.model.task_student_group.entity.GroupStudent;
import com.iecube.community.model.task_student_group.mapper.TaskStudentGroupMapper;
import com.iecube.community.model.task_student_group.service.ex.GroupLimitException;
import com.iecube.community.model.pst_article.entity.PSTArticle;
import com.iecube.community.model.pst_article.service.PSTArticleService;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.pst_article_compose.mapper.PSTArticleComposeMapper;
import com.iecube.community.model.question_bank.mapper.QuestionBankMapper;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.service.ex.NoQuestionException;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.mapper.TagMapper;
import com.iecube.community.model.task.entity.*;
import com.iecube.community.model.task.service.ex.GenStudentReportFiledException;
import com.iecube.community.model.task.service.ex.PSTResourceNotFoundException;
import com.iecube.community.model.task.service.ex.PermissionDeniedException;
import com.iecube.community.model.pst_resource.entity.PSTResource;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.pst_resource.mapper.PSTResourceMapper;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.model.task.service.ex.SQLBatchProcessingException;
import com.iecube.community.model.task.vo.TaskBriefVo;
import com.iecube.community.model.task_attention.entity.Attention;
import com.iecube.community.model.task_attention.entity.TaskAttention;
import com.iecube.community.model.task_attention.mapper.TaskAttentionMapper;
import com.iecube.community.model.task_back_drop.entity.BackDrop;
import com.iecube.community.model.task_back_drop.entity.TaskBackDrop;
import com.iecube.community.model.task_back_drop.mapper.BackDropMapper;
import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_deliverable_requirement.entity.TaskDeliverableRequirement;
import com.iecube.community.model.task_deliverable_requirement.mapper.DeliverableRequirementMapper;
import com.iecube.community.model.task_details.entity.Details;
import com.iecube.community.model.task_details.entity.TaskDetails;
import com.iecube.community.model.task_details.mapper.TaskDetailsMapper;
import com.iecube.community.model.task_e_md_proc.entity.TaskEMdProc;
import com.iecube.community.model.task_e_md_proc.mapper.TaskEMdProcMapper;
import com.iecube.community.model.task_experimental_subject.entity.ExperimentalSubject;
import com.iecube.community.model.task_experimental_subject.entity.TaskExperimentalSubject;
import com.iecube.community.model.task_experimental_subject.mapper.TaskExperimentalSubjectMapper;
import com.iecube.community.model.task_md_doc.entity.TaskMdDoc;
import com.iecube.community.model.task_md_doc.mapper.TaskMdDocMapper;
import com.iecube.community.model.task_reference_file.entity.TaskReferenceFile;
import com.iecube.community.model.task_reference_file.mapper.ReferenceFileMapper;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_reference_link.entity.TaskReferenceLink;
import com.iecube.community.model.task_reference_link.mapper.ReferenceLinkMapper;
import com.iecube.community.model.task_requirement.entity.Requirement;
import com.iecube.community.model.task_requirement.entity.TaskRequirement;
import com.iecube.community.model.task_requirement.mapper.RequirementMapper;
import com.iecube.community.util.pdf.MdArticleStudentReportGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private PSTResourceMapper pstResourceMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private BackDropMapper backDropMapper;

    @Autowired
    private RequirementMapper requirementMapper;
    @Autowired
    private DeliverableRequirementMapper deliverableRequirementMapper;

    @Autowired
    private ReferenceLinkMapper referenceLinkMapper;

    @Autowired
    private ReferenceFileMapper referenceFileMapper;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private TaskExperimentalSubjectMapper experimentalSubjectMapper;

    @Autowired
    private TaskAttentionMapper attentionMapper;

    @Autowired
    private TaskDetailsMapper taskDetailsMapper;

    @Autowired
    private TaskMdDocMapper taskMdDocMapper;

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private TaskStudentGroupMapper taskStudentGroupMapper;

    @Autowired
    private PSTArticleService pstArticleService;

    @Autowired
    private PSTArticleComposeMapper pstArticleComposeMapper;

    @Autowired
    private TaskEMdProcMapper taskEMdProcMapper;

    @Autowired
    private LabProcRefMapper labProcRefMapper;

    @Autowired
    private LabProcMapper labProcMapper;

    @Value("${generated-report}")
    private String genStudentReportDir;

    @Value("${resource-location}/image/")
    private String IMAGEPath;
    @Value("${font.path}")
    private String fontPath;

    @Value("${generated-report}")
    private String genFileDir;

    @Value("${spring.datasource.url}")
    private String DBUrl;

    @Value("${spring.datasource.username}")
    private String DBUsername;

    @Value("${spring.datasource.password}")
    private String DBPassword;

    /**
     * 任务状态
     * 0 未提交 => wait
     * 1 已提交文件 => process
     * 2 已完成content并点击保存 =>  finish
     * 3 教师已经提交批改
     */
    public static final int DEFAULT_STATUS=0; //未提交
    public static final int SUBMIT_FILE_STATUS=1; // 进行时，将要做的
    public static final int SUBMIT_CONTENT_STATUS=2; // 已提交

    public static final int TEACHER_READ_OVER=3; // 已批阅

    @Override
    public Task createTask(Task task, Integer user) {
        task.setTaskTemplateId(task.getId());
        task.setCreator(user);
        task.setLastModifiedUser(user);
        task.setCreateTime(new Date());
        task.setLastModifiedTime(new Date());
        Integer row = taskMapper.addProjectTask(task);
        if(row!=1){
            throw new InsertException("创建任务异常");
        }

        if(task.getBackDropList()!=null && task.getBackDropList().size()>0){
            for(BackDrop backDrop:task.getBackDropList()){
                Integer re = backDropMapper.insert(backDrop);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskBackDrop taskBackDrop = new TaskBackDrop();
                taskBackDrop.setTaskId(task.getId());
                taskBackDrop.setBackDropId(backDrop.getId());
                Integer co = backDropMapper.connect(taskBackDrop);
                if(co!=1){
                    throw new InsertException("关联任务-背景异常");
                }
            }
        }

        if(task.getRequirementList()!=null && task.getRequirementList().size()>0){
            for (Requirement requirement : task.getRequirementList()){
                Integer re = requirementMapper.insert(requirement);
                if (re != 1){
                    throw new InsertException("插入任务要求异常");
                }
                TaskRequirement taskRequirement = new TaskRequirement();
                taskRequirement.setTaskId(task.getId());
                taskRequirement.setRequirementId(requirement.getId());
                Integer co = requirementMapper.connect(taskRequirement);
                if(co != 1){
                    throw new InsertException("关联任务-任务要求异常");
                }
            }
        }

        // 任务交付物要求
        if(task.getDeliverableRequirementList()!=null && task.getDeliverableRequirementList().size()>0){
            for(DeliverableRequirement deliverableRequirement : task.getDeliverableRequirementList()){
                Integer re = deliverableRequirementMapper.insert(deliverableRequirement);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskDeliverableRequirement taskDeliverableRequirement = new TaskDeliverableRequirement();
                taskDeliverableRequirement.setTaskId(task.getId());
                taskDeliverableRequirement.setDeliverableRequirementId(deliverableRequirement.getId());
                Integer co = deliverableRequirementMapper.connect(taskDeliverableRequirement);
                if (co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务参考连接
        if(task.getReferenceFileList()!=null && task.getReferenceFileList().size()>0){
            for(Resource resource: task.getReferenceFileList()){
                TaskReferenceFile taskReferenceFile= new TaskReferenceFile();
                taskReferenceFile.setTaskId(task.getId());
                taskReferenceFile.setReferenceFileId(resource.getId());
                Integer co = referenceFileMapper.connect(taskReferenceFile);
                if(co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务参考文件
        if(task.getReferenceLinkList()!= null && task.getReferenceLinkList().size()>0){
            for(ReferenceLink referenceLink:task.getReferenceLinkList()){
                Integer re = referenceLinkMapper.insert(referenceLink);
                if (re != 1){
                    throw new InsertException("插入数据异常");
                }
                TaskReferenceLink taskReferenceLink = new TaskReferenceLink();
                taskReferenceLink.setTaskId(task.getId());
                taskReferenceLink.setReferenceLinkId(referenceLink.getId());
                Integer co = referenceLinkMapper.connect(taskReferenceLink);
                if(co !=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        //实验对象、器件
        if(task.getExperimentalSubjectList()!=null && task.getExperimentalSubjectList().size()>0){
            for(ExperimentalSubject experimentalSubject:task.getExperimentalSubjectList()){
                Integer re = experimentalSubjectMapper.insert(experimentalSubject);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskExperimentalSubject taskExperimentalSubject = new TaskExperimentalSubject();
                taskExperimentalSubject.setTaskId(task.getId());
                taskExperimentalSubject.setExperimentalSubjectId(experimentalSubject.getId());
                Integer co = experimentalSubjectMapper.connect(taskExperimentalSubject);
                if(co !=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 注意事项
        if(task.getAttentionList()!=null && task.getAttentionList().size()>0){
            for(Attention attention:task.getAttentionList()){
                Integer re = attentionMapper.insert(attention);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskAttention taskAttention = new TaskAttention();
                taskAttention.setTaskId(task.getId());
                taskAttention.setAttentionId(attention.getId());
                Integer co = attentionMapper.connect(taskAttention);
                if(co !=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }

        // markDown指导书
        if(task.getTaskMdDoc() != null){
            TaskMdDoc taskMdDoc = new TaskMdDoc();
            taskMdDoc.setTaskId(task.getId());
            taskMdDoc.setMdDocId(task.getTaskMdDoc());
            Integer co = taskMdDocMapper.connect(taskMdDoc);
            if(co != 1){
                throw new InsertException("插入数据异常");
            }
        }

        //任务详情
        if(task.getTaskDetails()!=null && !task.getTaskDetails().equals("")){
            Details details = new Details();
            details.setName(task.getTaskDetails());
            Integer re = taskDetailsMapper.insert(details);
            if(re!=1){
                throw new InsertException("插入数据异常");
            }
            TaskDetails taskDetails = new TaskDetails();
            taskDetails.setTaskId(task.getId());
            taskDetails.setDetailsId(details.getId());
            Integer co = taskDetailsMapper.connect(taskDetails);
            if(co !=1){
                throw new InsertException("插入数据异常");
            }
        }

        // emd
        if (task.getTaskEMdProc() != null){
            TaskEMdProc taskEMdProc = new TaskEMdProc();
            taskEMdProc.setTaskId(task.getId());
            taskEMdProc.setProcId(task.getTaskEMdProc());
            // 设置实验的参考内容 md格式的文本 --> 用于ai包含文件
            LabProc labProc = labProcMapper.getLabProcById(task.getTaskEMdProc());
            LabProcRef labProcRef = labProcRefMapper.getByLabId(task.getTaskEMdProc());
            taskEMdProc.setProcRef(labProcRef==null?"":labProcRef.getReference());
            taskEMdProc.setSectionPrefix(labProc.getSectionPrefix());
            int re = taskEMdProcMapper.taskAddProc(taskEMdProc);
            if(re != 1){
                throw new InsertException("插入数据异常");
            }
        }
        return task;
    }

    @Override
    public Task getById(Integer id) {
        Task res  = taskMapper.getTaskById(id);
        Project project;
        if(res.getTaskStartTime() == null || res.getTaskEndTime() == null){
           project =  projectMapper.findById(res.getProjectId());
            if(res.getTaskStartTime() == null){
                res.setTaskStartTime(project.getStartTime());
            }
            if(res.getTaskEndTime() == null){
                res.setTaskEndTime(project.getEndTime());
            }
        }
        return res;
    }

    @Override
    public List<ProjectStudentTask> getPSTByProjectId(Integer projectId) {
        return null;
    }
    @Override
    public List<StudentTaskDetailVo> findStudentTaskByProjectId(Integer projectId, Integer studentId){
        List<StudentTaskDetailVo> tasks = taskMapper.findStudentTaskByProjectId(projectId,studentId);
        for(StudentTaskDetailVo task:tasks){
            List<PSTResourceVo> taskResources = new ArrayList<>();
            List<PSTResource> pSTResources = pstResourceMapper.getPSTResourcesByPSTId(task.getPSTId());
            for(PSTResource pSTResource:pSTResources){
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
            task.setResources(taskResources);
            List<Tag> tags = tagMapper.getTagsByPSTId(task.getPSTId());
            task.setTaskTags(tags);
            try{
                List questions = questionBankService.getQuestions(task.getPSTId());
                task.setQuestionListSize(questions.size());
            }catch (NoQuestionException e ){
                task.setQuestionListSize(0);
            }
            //  null异常 处理  --- 清华版本中不执行 没有数据库表
            PSTArticle pstArticle = pstArticleService.getByPstId(task.getPSTId());
            if(pstArticle != null){
                task.setPstArticle(pstArticle);
            }
        }
        return tasks;
    }

    @Override
    public List<PSTResourceVo> findPSTResourceVo(Integer pstId) {
        List<PSTResourceVo> taskResources = new ArrayList<>();
        List<PSTResource> pSTResources = pstResourceMapper.getPSTResourcesByPSTId(pstId);
        for(PSTResource pSTResource:pSTResources){
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
        return taskResources;
    }

    @Override
    public StudentTaskDetailVo findStudentTaskByPSTId(Integer pstId) {
        StudentTaskDetailVo studentTaskDetail = taskMapper.findStudentTaskByPSTId(pstId);
        List<PSTResourceVo> taskResources = new ArrayList<>();
        List<PSTResource> pSTResources = pstResourceMapper.getPSTResourcesByPSTId(studentTaskDetail.getPSTId());
        for(PSTResource pSTResource:pSTResources){
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
        List<Tag> tags = tagMapper.getTagsByPSTId(studentTaskDetail.getPSTId());
        studentTaskDetail.setTaskTags(tags);
        studentTaskDetail.setResources(taskResources);
        return studentTaskDetail;
    }

    @Override
    public Void teacherModifyPST(ProjectStudentTaskQo projectStudentTaskQo) {
        Integer pstId = projectStudentTaskQo.getPSTId();
        // 教师提交的字段有  projectStudentTaskQo.getTaskEvaluate(); projectStudentTaskQo.getTaskGrade(); projectStudentTaskQo.getTaskTags();
        taskMapper.updatePSTEvaluate(pstId,projectStudentTaskQo.getTaskEvaluate());
        this.computeGrade(pstId, projectStudentTaskQo.getTaskGrade());
        this.autoComputeProjectGrade(pstId);
        taskMapper.updatePSTStatus(pstId,TEACHER_READ_OVER);
        List<Tag> oldTags = tagMapper.getTagsByPSTId(projectStudentTaskQo.getPSTId());
        List<Tag> newTags = projectStudentTaskQo.getTaskTags();
        if(oldTags.size()==0 && newTags.size()==0){
//            System.out.println("不处理");
            return null;
        }else if(oldTags.size()==0 && newTags.size()>0){
//            System.out.println("直接添加");
            for (Tag newTag : newTags){
                tagMapper.addTagToPST(pstId,newTag.getId());
            }
            return null;
        } else if (oldTags.size()>0 && newTags.size()==0) {
//            System.out.println("直接删除");
            for (Tag oldTag :oldTags){
                tagMapper.deletePSTTag(pstId, oldTag.getId());
            }
            return null;
        }
        //如果oldTag不在newTags 中 --> 删除
        //如果newTag不在oldTags 中 --> 新增
        for(Tag oldTag : oldTags){
            if(!newTags.contains(oldTag)){
//                System.out.println("删除");
                tagMapper.deletePSTTag(pstId,oldTag.getId());
            }
        }
        for(Tag newTag : newTags){
            if(!oldTags.contains(newTag)){
//                System.out.println("新增");
                tagMapper.addTagToPST(pstId, newTag.getId());
            }
        }
        return null;
    }

    @Override
    public void teacherModifyGroupPST(ProjectStudentTaskQo projectStudentTaskQo){
        Integer pstId = projectStudentTaskQo.getPSTId();
        // 根据pstId获取小组内的所有pst
        ProjectStudentTask projectStudentTask = taskMapper.getProjectStudentTaskById(pstId);
        Integer studentId = projectStudentTask.getStudentId();
        Integer projectId = projectStudentTask.getProjectId();
        List<GroupStudent> allGroupStudent = taskStudentGroupMapper.getGroupStudentByStudentId(studentId, projectId);
        if(allGroupStudent.size()==0){
            this.teacherModifyPST(projectStudentTaskQo);
        }else {
            List<ProjectStudentTask> allProjectStudentTask =
                    taskMapper.getProjectStudentTaskByProjectIdAndTaskId(projectStudentTask.getProjectId(),projectStudentTask.getTaskId());
            //这个是组内的所有人的pst
            List<ProjectStudentTask> groupProjectStudentTask = new ArrayList<>();
            for(int i=0; i<allGroupStudent.size();i++){
                for(int j=0; j<allProjectStudentTask.size();j++){
                    if(allGroupStudent.get(i).getStudentId().equals(allProjectStudentTask.get(j).getStudentId())){
                        groupProjectStudentTask.add(allProjectStudentTask.get(j));
                    }
                }
            }
            for(ProjectStudentTask oneOfProjectStudentTask:groupProjectStudentTask){
                projectStudentTaskQo.setPSTId(oneOfProjectStudentTask.getId());
                this.teacherModifyPST(projectStudentTaskQo);
            }
        }
    }

    private void computeGrade(Integer pstId, double reportGrade){
        Integer objectiveGrade = questionBankMapper.getObjectiveGrade(pstId);
        if(objectiveGrade == null){
            objectiveGrade=0;
        }
        Integer objectiveWeighting = questionBankMapper.getObjectiveWeighting(pstId);
        double grade = objectiveGrade*objectiveWeighting/100 + reportGrade*(100-objectiveWeighting)/100;
        taskMapper.updatePSTGrade(pstId, grade, reportGrade );
    }

    @Override
    public Void autoComputeProjectGrade(Integer pstId) {
        StudentTaskDetailVo studentTaskDetailVo = taskMapper.findStudentTaskByPSTId(pstId);
        Integer studentId = studentTaskDetailVo.getStudentId();
        Integer projectId = studentTaskDetailVo.getProjectId();
        ProjectStudentVo ps = projectMapper.findProjectStudent(projectId,studentId);
        List<StudentTaskDetailVo> studentTaskDetailVoList = this.findStudentTaskByProjectId(projectId,studentId);
        double psNewGrade = 0.0;
        for(StudentTaskDetailVo taskDetailVo: studentTaskDetailVoList){
            if(taskDetailVo.getTaskGrade() != null){
                double grade = (taskDetailVo.getWeighting()/100) * taskDetailVo.getTaskGrade();
                psNewGrade = psNewGrade + grade;
            }
        }
        projectMapper.updateProjectStudentGrade(ps.getPsId(),psNewGrade);
        return null;
    }

    public static double getAverage(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        double average =  sum / list.size();
        return Math.round(average * 100.0) / 100.0;
    }

    /**
     * 教师批阅学生提交的pdf文档
     * @param file 批阅后的文件
     * @param pstRId  表pst_resource的id
     * @param teacherId 教师id， 权限判断
     * @return null
     */
    @Override
    public void teacherReadOverStudentSubmitPdf(MultipartFile file, Integer pstRId,
                                                                Integer teacherId) throws IOException {
        PSTResource pstResource = pstResourceMapper.getById(pstRId);
        Resource studentResource = resourceMapper.getById(pstResource.getResourceId());
        String readOverOriginFilename = "已批阅-"+studentResource.getOriginFilename();
        Resource newReadOver =  resourceService.UploadReadOverPDF(file, readOverOriginFilename, teacherId);
        if (pstResource.getReadOverResourceId() != null){
            resourceService.deleteById(pstResource.getReadOverResourceId());
        }
        pstResource.setReadOverResourceId(newReadOver.getId());
        pstResourceMapper.updatePSTResource(pstResource);
    }

    @Override
    public void teacherReadOverGroupSubmitPdf(MultipartFile file, Integer pstRId, Integer teacherId) throws IOException{
        //通过pstRId 获取pstId
        PSTResource pstResource = pstResourceMapper.getById(pstRId);
        Integer pstId = pstResource.getPSTId();
        // 根据pstId获取小组内的所有pst
        ProjectStudentTask projectStudentTask = taskMapper.getProjectStudentTaskById(pstId);
        Integer studentId = projectStudentTask.getStudentId();
        Integer projectId = projectStudentTask.getProjectId();
        List<GroupStudent> allGroupStudent = taskStudentGroupMapper.getGroupStudentByStudentId(studentId, projectId);
        if(allGroupStudent.size()==0){
            this.teacherReadOverStudentSubmitPdf(file,pstRId, teacherId);
        }else {
            List<ProjectStudentTask> allProjectStudentTask =
                    taskMapper.getProjectStudentTaskByProjectIdAndTaskId(projectStudentTask.getProjectId(),projectStudentTask.getTaskId());
            //这个是组内的所有人的pst
            List<ProjectStudentTask> groupProjectStudentTask = new ArrayList<>();
            for(int i=0; i<allGroupStudent.size();i++){
                for(int j=0; j<allProjectStudentTask.size();j++){
                    if(allGroupStudent.get(i).getStudentId().equals(allProjectStudentTask.get(j).getStudentId())){
                        groupProjectStudentTask.add(allProjectStudentTask.get(j));
                    }
                }
            }
            for(ProjectStudentTask oneOfProjectStudentTask:groupProjectStudentTask){
                List<PSTResource> pstResourceList = pstResourceMapper.getPSTResourcesByPSTId(oneOfProjectStudentTask.getId());
                for (PSTResource pstResource1 : pstResourceList){
                    this.teacherReadOverStudentSubmitPdf(file, pstResource1.getId(), teacherId);
                }
            }
        }

    }

    @Override
    public List<TaskVo> studentGetProjectTasks(Integer projectId) {
        List<TaskVo> tasks = taskMapper.findByProjectId(projectId);
        for(TaskVo task :tasks){
            List<BackDrop> backDropList = backDropMapper.getBackDropByTaskId(task.getId());
            task.setBackDrops(backDropList);
            List<Requirement> requirements = requirementMapper.getRequirementsByTaskId(task.getId());
            task.setTaskTargets(requirements);
            List<DeliverableRequirement> deliverableRequirements = deliverableRequirementMapper.getDeliverableRequirementByTaskId(task.getId());
            task.setTaskDeliverables(deliverableRequirements);
            List<ReferenceLink> referenceLinks = referenceLinkMapper.getReferenceLinksByTaskId(task.getId());
            task.setTaskReferenceLinks(referenceLinks);
            List<Resource> resources = referenceFileMapper.getReferenceFilesByTaskId(task.getId());
            task.setTaskReferenceFiles(resources);
            List<ExperimentalSubject> experimentalSubjectList = experimentalSubjectMapper.getExperimentalSubjectByTaskId(task.getId());
            task.setExperimentalSubjectList(experimentalSubjectList);
            List<Attention> attentionList = attentionMapper.getAttentionByTaskId(task.getId());
            task.setAttentionList(attentionList);
            Details details = taskDetailsMapper.getDetailsByTaskId(task.getId());
            if(details!=null){
                task.setTaskDetails(details.getName());
            }
            // null异常 处理
            TaskMdDoc taskMdDoc = taskMdDocMapper.getTaskMdDocByTask(task.getId());
            if(taskMdDoc!=null){
                task.setTaskMdDoc(taskMdDoc.getMdDocId());
                MDChapter mdChapter = markdownService.getChapterById(taskMdDoc.getMdDocId());
                task.setMdChapter(mdChapter);
            }
            LabProc labProc = taskEMdProcMapper.getLabProcByTaskId(task.getId());
            if(labProc!=null){
                task.setLabProc(labProc);
                task.setTaskEMDProc(labProc.getId());
            }
        }
        return tasks;
    }

    /**
     * 学生提交文件
     * @param file
     * @param pstId
     * @param studentId
     * @return
     * @throws IOException
     */
    @Override
    public StudentTaskDetailVo submitFile(MultipartFile file, Integer pstId, Integer studentId) throws IOException {
        StudentTaskDetailVo oldTaskDetail = this.findStudentTaskByPSTId(pstId);
        //通过pstId查询task
//        Task task = taskMapper.findTaskByPSTId(pstId);
        //判断是否在时间内 如果不在时间内 是否为resubmit
//        Date CurrDate = new Date();
//        if(CurrDate.after(task.getTaskEndTime()) || task.getTaskStartTime().after(CurrDate)){
//            if(oldTaskDetail.getTaskResubmit()!=1){
//                throw new PermissionDeniedException("不在任务时间内");
//            }
//        }
        //保存文件
        file.getOriginalFilename();
        String fileType = file.getContentType();
        Resource resource = new Resource();
        if (fileType.contains("image")){
            resource =  resourceService.UploadImage(file,studentId);
        }else {
            resource =  resourceService.UploadFile(file, studentId);
        }
        PSTResource pstResource = new PSTResource();
        pstResource.setPSTId(pstId);
        pstResource.setResourceId(resource.getId());
        Integer row =  pstResourceMapper.add(pstResource);
        if( row!=1 ){
            resourceService.deleteById(resource.getId());
            throw new InsertException("任务关联文件失败");
        }
        Integer row1 = taskMapper.updatePSTStatus(pstId, SUBMIT_FILE_STATUS);
        if( row1 != 1){
            throw new InsertException("更改任务状态失败");
        }
        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(pstId);
        return taskDetail;
    }

    @Override
    public StudentTaskDetailVo deleteStudentSubmitFile(Integer PSTResourceId, Integer studentId) {
        PSTResource pstResource = pstResourceMapper.getById(PSTResourceId);
        if(pstResource == null){
            throw new PSTResourceNotFoundException("未找到该提交的文件");
        }
        if(pstResource.getReadOverResourceId() != null){
            throw new PermissionDeniedException("已批阅，不允许删除");
        }
        resourceService.deleteById(pstResource.getResourceId());
        //会报异常
        Integer row =  pstResourceMapper.deleteById(pstResource.getId());
        if(row!=1){
            throw new DeleteException("文件已删除，数据更新失败");
        }
        List<PSTResource> newPstResource = pstResourceMapper.getPSTResourcesByPSTId(pstResource.getPSTId());
        if(newPstResource.size()==0){
            //如果文件删完了，将status置0
            taskMapper.updatePSTStatus(pstResource.getPSTId(), DEFAULT_STATUS);
        }
        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(pstResource.getPSTId());
        return taskDetail;
    }

    @Override
    public StudentTaskDetailVo studentSubmitContent(String content, Integer pstId, Integer studentId) {
        StudentTaskDetailVo oldTaskDetail = this.findStudentTaskByPSTId(pstId);
        //通过pstId查询task
        Task task = taskMapper.findTaskByPSTId(pstId);
        //判断是否在时间内 如果不在时间内 是否为resubmit
//        Date CurrDate = new Date();

//        if(CurrDate.after(task.getTaskEndTime()) || task.getTaskStartTime().after(CurrDate)){
//            if(oldTaskDetail.getTaskResubmit()!=1){
//                throw new PermissionDeniedException("不在任务时间内");
//            }
//        }
        //判断是否已经提交过文件， 如果没有提交文件则不能提交content
//        List<PSTResource> pstResource = pstResourceMapper.getPSTResourcesByPSTId(pstId);
//        if (pstResource.size()==0){
//            throw new PermissionDeniedException("未提交文件/文档，请先提交文件/文档");
//        }
        Integer row = taskMapper.updatePSTContent(pstId, content);
        if (row!=1){
            throw new UpdateException("更新提交内容失败");
        }
        Integer row1 = taskMapper.updatePSTStatus(pstId, SUBMIT_CONTENT_STATUS);
        if (row1 !=1){
            throw new UpdateException("更改任务状态失败");
        }
        taskMapper.updatePSTResubmit(pstId,0);
        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(pstId);
        return taskDetail;
    }

    @Override
    public StudentTaskDetailVo studentSubmit(Integer pstId, Integer studentId) {
        StudentTaskDetailVo thisStudentTaskDetail = this.findStudentTaskByPSTId(pstId);
        Task task = taskMapper.findTaskByPSTId(pstId);
        Project project = projectMapper.findByPstId(pstId);
        //判断是否在时间内 如果不在时间内 是否为resubmit
//        Date CurrDate = new Date();

//        if(CurrDate.after(task.getTaskEndTime()) || task.getTaskStartTime().after(CurrDate)){
//            if(oldTaskDetail.getTaskResubmit()!=1){
//                throw new PermissionDeniedException("不在任务时间内");
//            }
//        }
        if (project.getUseGroup() == 1 && project.getMdCourse() != null){
            Group group = taskStudentGroupMapper.getGroupByTaskStudent(project.getId(), studentId);
            if(group == null){
                throw new GroupLimitException("本课程为分组实验进行， 请先创建小组或加入小组");
            }
            return this.mdGroupSubmit(thisStudentTaskDetail, group);
        }else{
            return this.noGroupSubmit(pstId);
        }
    }

    public StudentTaskDetailVo noGroupSubmit(Integer pstId){
        Integer row1 = taskMapper.updatePSTStatus(pstId, SUBMIT_CONTENT_STATUS);
        if (row1 !=1){
            throw new UpdateException("更改任务状态失败");
        }
        taskMapper.updatePSTResubmit(pstId,0);
        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(pstId);
        return taskDetail;
    }

    public StudentTaskDetailVo mdGroupSubmit(StudentTaskDetailVo thisStudentTaskDetail,Group group){
        List<PSTArticleCompose> groupPstArticleComposeList = pstArticleComposeMapper.composeListByPstId(thisStudentTaskDetail.getPSTId());
        List<Integer> pstIdList = taskStudentGroupMapper.getPstListByGroupAndTaskId(group.getId(), thisStudentTaskDetail.getTaskNum()); // 组内的pstId
        for(Integer pstId: pstIdList){
            taskMapper.updatePSTStatus(pstId, SUBMIT_CONTENT_STATUS);
            taskMapper.updatePSTResubmit(pstId,0);
        }
        pstIdList.remove(thisStudentTaskDetail.getPSTId());
        // 根据 pstId 更改对应的pstArticle 的 pstArticleCompose
        List<PSTArticleCompose> willUpdatePstArticleComposeList = new ArrayList<>();
        for(Integer pstId : pstIdList){
            List<PSTArticleCompose> thisStudentPstArticleComposeList = pstArticleComposeMapper.composeListByPstId(pstId);
            for(int i=0; i<groupPstArticleComposeList.size(); i++){
                PSTArticleCompose willUpdate = thisStudentPstArticleComposeList.get(i);
                willUpdate.setVal(groupPstArticleComposeList.get(i).getVal());
                willUpdate.setStatus(groupPstArticleComposeList.get(i).getStatus());
                willUpdate.setResult(groupPstArticleComposeList.get(i).getResult());
//                pstArticleComposeMapper.composeUpdateByPstIdIndex(willUpdate);
                willUpdatePstArticleComposeList.add(willUpdate);
            }

        }
        String updateSql = "UPDATE pst_article_compose SET `val`=?,`result`=?, `status`=? WHERE `pst_id`=? and `index`=?";
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUsername, DBPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            connection.setAutoCommit(false); // 关闭自动提交
            // 假设要更新的数据
            for (int i = 1; i < willUpdatePstArticleComposeList.size(); i++) {
                preparedStatement.setString(1, willUpdatePstArticleComposeList.get(i).getVal());
                if(willUpdatePstArticleComposeList.get(i).getResult() != null){
                    preparedStatement.setDouble(2, willUpdatePstArticleComposeList.get(i).getResult());
                }else{
                    preparedStatement.setNull(2, java.sql.Types.DOUBLE);
                }
                preparedStatement.setInt(3, willUpdatePstArticleComposeList.get(i).getStatus());
                preparedStatement.setInt(4, willUpdatePstArticleComposeList.get(i).getPstId());
                preparedStatement.setInt(5, willUpdatePstArticleComposeList.get(i).getIndex());
                preparedStatement.addBatch(); // 将更新操作添加到批处理中

                if (i % 100 == 0) { // 每100条执行一次批处理
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch(); // 执行剩余的批处理
            connection.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLBatchProcessingException(e.getMessage());
        }
        log.info("学生"+thisStudentTaskDetail.getStudentId()+"提交了小组实验报告: taskId: "+thisStudentTaskDetail.getProjectId()+ " PSTid: "+thisStudentTaskDetail.getPSTId());

        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(thisStudentTaskDetail.getPSTId());
        taskStudentGroupMapper.updateGroupSubmitted(group.getId(), 2, thisStudentTaskDetail.getStudentId());
        return taskDetail;
    }

    /**
     * 学生将任务状态从2 修改为1
     * @param pstId
     * @return
     */
    @Override
    public StudentTaskDetailVo studentChangeStatus(Integer pstId) {
        taskMapper.updatePSTStatus(pstId, SUBMIT_FILE_STATUS);
        StudentTaskDetailVo taskDetail = this.findStudentTaskByPSTId(pstId);
        return taskDetail;
    }

    @Override
    public List<TaskVo> getProjectTasks(Integer projectId) {
        List<TaskVo> taskVoList = taskMapper.getProjectTasks(projectId);
        return taskVoList;
    }

    @Override
    public Void updateDataTables(Integer pstId, String dataTables) {
        Integer row = taskMapper.updatePSTDataTables(pstId, dataTables);
        if(row != 1){
            throw new UpdateException("更新数据失败");
        }
        return null;
    }

    @Override
    public List<TaskBriefVo> getProjectTaskBriefList(Integer projectId) {
        List<TaskBriefVo> result = taskMapper.getProjectBriefVo(projectId);
        return result;
    }

    @Override
    public List<PSTBaseDetail> getPSTBaseDetailByProject(Integer projectId) {
        List<PSTBaseDetail> pstBaseDetailList = taskMapper.getPSTBaseDetailByProject(projectId);
        return pstBaseDetailList;
    }

    public PSTBaseDetail noGroupReadOverPSTArticle(Integer pstId, Integer teacherId){
        Double grade = pstArticleService.computeGrade(pstId);
        taskMapper.updatePSTGrade(pstId, grade,grade);
        taskMapper.updatePSTStatus(pstId, TEACHER_READ_OVER);
        // 报告
        PSTBaseDetail pstBaseDetail = taskMapper.getPstBaseDetail(pstId);
        this.autoComputeProjectGrade(pstId);
        genMdArticleReport(pstId, pstBaseDetail, teacherId);
        return pstBaseDetail;
    }

    @Override
    public PSTBaseDetail mdGroupReadOverPSTArticle(Integer projectId, Integer pstId, Integer teacherId){
        //1. 同步每个compose的成绩
        //2. 同步最终成绩，同步批阅状态
        //3. 生成报告
        ProjectStudentTask PST = taskMapper.getProjectStudentTaskById(pstId);
        Integer studentId = PST.getStudentId();
        Group group = taskStudentGroupMapper.getGroupByTaskStudent(projectId, studentId);
        StudentTaskDetailVo thisStudentTaskDetail = this.findStudentTaskByPSTId(pstId);
        Double grade = pstArticleService.computeGrade(pstId);
        List<PSTArticleCompose> groupPstArticleComposeList = pstArticleComposeMapper.composeListByPstId(pstId); // 组内最终成绩的来源
        List<Integer> pstIdList = taskStudentGroupMapper.getPstListByGroupAndTaskId(group.getId(), thisStudentTaskDetail.getTaskNum()); // 组内的pstId
        List<PSTArticleCompose> willUpdatePstArticleComposeList = new ArrayList<>();
        for(Integer p: pstIdList){
            taskMapper.updatePSTGrade(p, grade, grade);
            taskMapper.updatePSTStatus(p, TEACHER_READ_OVER);
            this.autoComputeProjectGrade(pstId);
            if(!p.equals(pstId)) {
                List<PSTArticleCompose> thisStudentPstArticleComposeList = pstArticleComposeMapper.composeListByPstId(pstId);
                for (int i = 0; i < thisStudentPstArticleComposeList.size(); i++) {
                    PSTArticleCompose willUpdate = thisStudentPstArticleComposeList.get(i);
                    willUpdate.setResult(groupPstArticleComposeList.get(i).getResult());
                    willUpdatePstArticleComposeList.add(willUpdate);
                }
            }
        }
        String updateSql = "UPDATE pst_article_compose SET `result`=? WHERE `pst_id`=? and `index`=?";
        try (Connection connection = DriverManager.getConnection(DBUrl, DBUsername, DBPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            connection.setAutoCommit(false); // 关闭自动提交
            // 假设要更新的数据
            for (int i = 1; i < willUpdatePstArticleComposeList.size(); i++) {
                if(willUpdatePstArticleComposeList.get(i).getResult() != null){
                    preparedStatement.setDouble(1, willUpdatePstArticleComposeList.get(i).getResult());
                }else{
                    preparedStatement.setNull(1, java.sql.Types.DOUBLE);
                }
                preparedStatement.setInt(2, willUpdatePstArticleComposeList.get(i).getPstId());
                preparedStatement.setInt(3, willUpdatePstArticleComposeList.get(i).getIndex());
                preparedStatement.addBatch(); // 将更新操作添加到批处理中

                if (i % 100 == 0) { // 每100条执行一次批处理
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch(); // 执行剩余的批处理
            connection.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLBatchProcessingException(e.getMessage());
        }
        PSTBaseDetail result = taskMapper.getPstBaseDetail(pstId);
        return result;
    }

    @Override
    @Async
    public void genMdArticleReportGroup(Integer projectId, Integer pstId, Integer teacherId){
        ProjectStudentTask PST = taskMapper.getProjectStudentTaskById(pstId);
        Integer studentId = PST.getStudentId();
        Group group = taskStudentGroupMapper.getGroupByTaskStudent(projectId, studentId);
        StudentTaskDetailVo thisStudentTaskDetail = this.findStudentTaskByPSTId(pstId);
        List<Integer> pstIdList = taskStudentGroupMapper.getPstListByGroupAndTaskId(group.getId(), thisStudentTaskDetail.getTaskNum()); // 组内的pstId
        for(Integer p:pstIdList){
            PSTBaseDetail pstBaseDetail = taskMapper.getPstBaseDetail(p);
            genMdArticleReport(pstId, pstBaseDetail, teacherId);
        }
    }

    @Override
    @Async
    public void genMdArticleReport(Integer pstId , PSTBaseDetail pstBaseDetail, Integer teacherId){
        PSTArticle pstArticle = pstArticleService.getByPstId(pstId);
        MdArticleStudentReportGen mdArticleStudentReportGen = new MdArticleStudentReportGen(genFileDir, IMAGEPath, fontPath);
        MultipartFile readOverReport = null;
        try{
            readOverReport =  mdArticleStudentReportGen.startGen(pstBaseDetail, pstArticle.getComposeList());
        }catch (Exception e){
            e.printStackTrace();
           throw new GenStudentReportFiledException(pstId+"学生报告生成异常");
        }
        try{
            Resource resource = resourceService.UploadFile(readOverReport, teacherId);
            List<PSTResource> pstResourceList = pstResourceMapper.getPSTResourcesByPSTId(pstId);
            if(pstResourceList.size()>0){
                for(PSTResource pstResource:pstResourceList){
                    if(pstResource.getReadOverResourceId()!=null){
                        resourceService.deleteById(pstResource.getReadOverResourceId());
                        pstResourceMapper.deleteById(pstResource.getId());
                    }
                }
            }
            PSTResource newPstResource = new PSTResource();
            newPstResource.setPSTId(pstId);
            newPstResource.setReadOverResourceId(resource.getId());
            int res = pstResourceMapper.add(newPstResource);
        }catch (Exception e){
            throw new GenStudentReportFiledException(pstId+"学生报告持久化异常");
        }
    }

}
