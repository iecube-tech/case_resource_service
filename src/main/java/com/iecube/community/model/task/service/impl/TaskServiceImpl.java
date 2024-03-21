package com.iecube.community.model.task.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.question_bank.mapper.QuestionBankMapper;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.service.ex.NoQuestionException;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.tag.mapper.TagMapper;
import com.iecube.community.model.task.entity.*;
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
import com.iecube.community.model.task_experimental_subject.entity.ExperimentalSubject;
import com.iecube.community.model.task_experimental_subject.entity.TaskExperimentalSubject;
import com.iecube.community.model.task_experimental_subject.mapper.TaskExperimentalSubjectMapper;
import com.iecube.community.model.task_reference_file.entity.TaskReferenceFile;
import com.iecube.community.model.task_reference_file.mapper.ReferenceFileMapper;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_reference_link.entity.TaskReferenceLink;
import com.iecube.community.model.task_reference_link.mapper.ReferenceLinkMapper;
import com.iecube.community.model.task_requirement.entity.Requirement;
import com.iecube.community.model.task_requirement.entity.TaskRequirement;
import com.iecube.community.model.task_requirement.mapper.RequirementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
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
    private TagMapper tagMapper;

    @Autowired
    private QuestionBankService questionBankService;

    /**
     * 任务状态
     * 0 未提交 => wait
     * 1 已提交文件 => process
     * 2 已完成content并点击保存 =>  finish
     * 3 教师已经提交批改
     */
    public static final int DEFAULT_STATUS=0;
    public static final int SUBMIT_FILE_STATUS=1;
    public static final int SUBMIT_CONTENT_STATUS=2;

    public static final int TEACHER_READ_OVER=3;

    @Override
    public Integer createTask(Task task, Integer user) {
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

        return task.getId();
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

        }
        return tasks;
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
     * @param filename 原学生提交的文档的文件名
     * @param pstRId  表pst_resource的id
     * @param teacherId 教师id， 权限判断
     * @return null
     */
    @Override
    public Void teacherReadOverStudentSubmitPdf(MultipartFile file, String filename, Integer pstRId,
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
        return null;
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

}
