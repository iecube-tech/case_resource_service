package com.iecube.community.model.taskTemplate.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.service.MarkdownService;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.entity.TaskTemplate;
import com.iecube.community.model.taskTemplate.mapper.TaskTemplateMapper;
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
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
import com.iecube.community.model.task_e_md_proc.entity.TaskTemplateEMdProc;
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
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskTemplateServiceImpl implements TaskTemplateService {

    @Autowired
    private TaskTemplateMapper taskTemplateMapper;

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
    private TaskAttentionMapper taskAttentionMapper;

    @Autowired
    private TaskExperimentalSubjectMapper experimentalSubjectMapper;

    @Autowired
    private TaskDetailsMapper taskDetailsMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private TaskMdDocMapper taskMdDocMapper;

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private TaskEMdProcMapper taskEMdProcMapper;

    @Override
    public void addTaskTemplateToContent(TaskTemplateDto taskTemplateDto, Integer user){
        //判断任务num是不是已经存在，存在则不能添加  1<=num<=5
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setContentId(taskTemplateDto.getContentId());
        taskTemplate.setTaskName(taskTemplateDto.getTaskName());
        taskTemplate.setNum(taskTemplateDto.getNum());
        taskTemplate.setWeighting(taskTemplateDto.getWeighting());
        taskTemplate.setClassHour(taskTemplateDto.getClassHour());
        taskTemplate.setTaskCover(taskCover.codeOf(taskTemplateDto.getNum()).getFiled());
        taskTemplate.setTaskDevice(taskTemplateDto.getTaskDevice());
        taskTemplate.setCreator(user);
        taskTemplate.setLastModifiedUser(user);
        taskTemplate.setCreateTime(new Date());
        taskTemplate.setLastModifiedTime(new Date());
        Integer row = taskTemplateMapper.insert(taskTemplate);
        if (row != 1){
            throw new InsertException("插入数据异常");
        }
        taskTemplateDto.setId(taskTemplate.getId());
        this.addTaskTemplateItems(taskTemplateDto);
    }

    public void addTaskTemplateItems(TaskTemplateDto taskTemplateDto){
        //backdrop
        if(taskTemplateDto.getBackDropList()!= null && taskTemplateDto.getBackDropList().size()>0){
            for(BackDrop backDrop:taskTemplateDto.getBackDropList()){
                Integer re = backDropMapper.insert(backDrop);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskBackDrop taskBackDrop = new TaskBackDrop();
                taskBackDrop.setTaskTemplateId(taskTemplateDto.getId());
                taskBackDrop.setBackDropId(backDrop.getId());
                Integer co = backDropMapper.connect(taskBackDrop);
                if(co!=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }

        // 任务要求 requirements
        if(taskTemplateDto.getRequirementList()!=null && taskTemplateDto.getRequirementList().size()>0){
            for (Requirement requirement: taskTemplateDto.getRequirementList()){
                Integer re = requirementMapper.insert(requirement);
                if (re != 1){
                    throw new InsertException("插入数据异常");
                }
                TaskRequirement taskRequirement = new TaskRequirement();
                taskRequirement.setTaskTemplateId(taskTemplateDto.getId());
                taskRequirement.setRequirementId(requirement.getId());
                Integer co = requirementMapper.connect(taskRequirement);
                if(co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务交付物要求 deliverableRequirements
        if(taskTemplateDto.getDeliverableRequirementList()!= null && taskTemplateDto.getDeliverableRequirementList().size()>0){
            for(DeliverableRequirement deliverableRequirement : taskTemplateDto.getDeliverableRequirementList()){
                Integer re = deliverableRequirementMapper.insert(deliverableRequirement);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskDeliverableRequirement taskDeliverableRequirement = new TaskDeliverableRequirement();
                taskDeliverableRequirement.setTaskTemplateId(taskTemplateDto.getId());
                taskDeliverableRequirement.setDeliverableRequirementId(deliverableRequirement.getId());
                Integer co = deliverableRequirementMapper.connect(taskDeliverableRequirement);
                if (co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }

//        // 任务参考文件 单独另外添加的
//        if(taskTemplateDto.getReferenceFileList()!=null && taskTemplateDto.getReferenceFileList().size()>0){
//            for(Resource resource: taskTemplateDto.getReferenceFileList()){
//                TaskReferenceFile taskReferenceFile= new TaskReferenceFile();
//                taskReferenceFile.setTaskTemplateId(taskTemplateDto.getId());
//                taskReferenceFile.setReferenceFileId(resource.getId());
//                Integer co = referenceFileMapper.connect(taskReferenceFile);
//                if(co != 1){
//                    throw new InsertException("插入数据异常");
//                }
//            }
//        }
        // 任务参考连接 referenceLinks
        if(taskTemplateDto.getReferenceLinkList()!= null && taskTemplateDto.getReferenceLinkList().size()>0){
            for(ReferenceLink referenceLink:taskTemplateDto.getReferenceLinkList()){
                Integer re = referenceLinkMapper.insert(referenceLink);
                if (re != 1){
                    throw new InsertException("插入数据异常");
                }
                TaskReferenceLink taskReferenceLink = new TaskReferenceLink();
                taskReferenceLink.setTaskTemplateId(taskTemplateDto.getId());
                taskReferenceLink.setReferenceLinkId(referenceLink.getId());
                Integer co = referenceLinkMapper.connect(taskReferenceLink);
                if(co !=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }

        //注意事项 taskAttention
        if(taskTemplateDto.getAttentionList()!=null && taskTemplateDto.getAttentionList().size()>0){
            for(Attention attention:taskTemplateDto.getAttentionList()){
                Integer re = taskAttentionMapper.insert(attention);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskAttention taskAttention = new TaskAttention();
                taskAttention.setTaskTemplateId(taskTemplateDto.getId());
                taskAttention.setAttentionId(attention.getId());
                Integer co = taskAttentionMapper.connect(taskAttention);
                if(co!=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        //实验器件 experimentalSubject
        if(taskTemplateDto.getExperimentalSubjectList()!=null && taskTemplateDto.getExperimentalSubjectList().size()>0){
            for(ExperimentalSubject experimentalSubject:taskTemplateDto.getExperimentalSubjectList()){
                Integer re = experimentalSubjectMapper.insert(experimentalSubject);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskExperimentalSubject taskExperimentalSubject = new TaskExperimentalSubject();
                taskExperimentalSubject.setTaskTemplateId(taskTemplateDto.getId());
                taskExperimentalSubject.setExperimentalSubjectId(experimentalSubject.getId());
                Integer co = experimentalSubjectMapper.connect(taskExperimentalSubject);
                if(co!=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }

        //实验指导书
        if(taskTemplateDto.getTaskMdDoc() != null){
            TaskMdDoc taskMdDoc = new TaskMdDoc();
            taskMdDoc.setMdDocId(taskTemplateDto.getTaskMdDoc());
            taskMdDoc.setTaskTemplateId(taskTemplateDto.getId());
            Integer co = taskMdDocMapper.connect(taskMdDoc);
            if(co!=1){
                throw new InsertException("插入数据异常");
            }
        }

        // EMD 指导书
        if(taskTemplateDto.getTaskEMdProc() != null){
            TaskTemplateEMdProc taskTemplateEMdProc = new TaskTemplateEMdProc();
            taskTemplateEMdProc.setTaskTemplateId(taskTemplateDto.getId());
            taskTemplateEMdProc.setProcId(taskTemplateDto.getTaskEMdProc());
            int re = taskEMdProcMapper.taskTemplateAddProc(taskTemplateEMdProc);
            if(re!=1){
                throw new InsertException("插入数据异常");
            }
        }

        //实验内容 taskDetails
        if(taskTemplateDto.getTaskDetails() != null){
            Details details=new Details();
            details.setName(taskTemplateDto.getTaskDetails());
            Integer re = taskDetailsMapper.insert(details);
            if(re!=1){
                throw new InsertException("插入数据异常");
            }
            TaskDetails taskDetails = new TaskDetails();
            taskDetails.setTaskTemplateId(taskTemplateDto.getId());
            taskDetails.setDetailsId(details.getId());
            Integer co = taskDetailsMapper.connect(taskDetails);
            if(co != 1){
                throw new InsertException("插入数据异常");
            }
        }
    }

    @Override
    public void updateTaskTemplate(TaskTemplateDto taskTemplateDto, Integer user) {
        TaskTemplate taskTemplate = new TaskTemplate();
        if(taskTemplateDto.getId()==null){
            throw new UpdateException("数据异常，无法更新");
        }
        taskTemplate.setId(taskTemplateDto.getId());
        taskTemplate.setContentId(taskTemplateDto.getContentId());
        taskTemplate.setTaskName(taskTemplateDto.getTaskName());
        taskTemplate.setNum(taskTemplateDto.getNum());
        taskTemplate.setWeighting(taskTemplateDto.getWeighting());
        taskTemplate.setClassHour(taskTemplateDto.getClassHour());
        taskTemplate.setTaskCover(taskCover.codeOf(taskTemplateDto.getNum()).getFiled());
        taskTemplate.setTaskDevice(taskTemplateDto.getTaskDevice());
        taskTemplate.setLastModifiedUser(user);
        taskTemplate.setLastModifiedTime(new Date());
        taskTemplateMapper.update(taskTemplate);
        this.deleteTaskTemplateItem(taskTemplateDto.getId());
        this.addTaskTemplateItems(taskTemplateDto);
    }

    @Override
    public List<TaskTemplateDto> findTaskTemplateByContent(Integer contentId) {
        List<TaskTemplateDto> contentTaskTemplates = taskTemplateMapper.getTaskTemplatesByContentId(contentId);
        for(TaskTemplateDto taskTemplateDto:contentTaskTemplates){
            List<BackDrop> backDropList = backDropMapper.getBackDropByTaskTemplateId(taskTemplateDto.getId());
            List<Requirement> requirementList = requirementMapper.getRequirementsByTaskTemplateId(taskTemplateDto.getId());
            List<DeliverableRequirement> deliverableRequirementList = deliverableRequirementMapper.getDeliverableRequirementByTaskTemplateId(taskTemplateDto.getId());
            List<ReferenceLink> referenceLinkList = referenceLinkMapper.getReferenceLinksByTaskTemplateId(taskTemplateDto.getId());
            List<Resource> referenceFileList = referenceFileMapper.getReferenceFilesByTaskTemplateId(taskTemplateDto.getId());
            List<Attention> attentionList = taskAttentionMapper.getAttentionByTaskTemplateId(taskTemplateDto.getId());
            List<ExperimentalSubject> experimentalSubjectList = experimentalSubjectMapper.getExperimentalSubjectByTaskTemplateId(taskTemplateDto.getId());
            Details details = taskDetailsMapper.getDetailsByTaskTemplateId(taskTemplateDto.getId());
            TaskMdDoc taskMdDoc = taskMdDocMapper.getTaskMdDocByTaskTemplateId(taskTemplateDto.getId());
            LabProc labProc = taskEMdProcMapper.getLabProcByTaskTemplateId(taskTemplateDto.getId());
            TaskTemplateEMdProc taskTemplateEMdProc = taskEMdProcMapper.getByTaskTemplateId(taskTemplateDto.getId());
            taskTemplateDto.setBackDropList(backDropList);
            taskTemplateDto.setRequirementList(requirementList);
            taskTemplateDto.setDeliverableRequirementList(deliverableRequirementList);
            taskTemplateDto.setReferenceLinkList(referenceLinkList);
            taskTemplateDto.setReferenceFileList(referenceFileList);
            taskTemplateDto.setAttentionList(attentionList);
            taskTemplateDto.setExperimentalSubjectList(experimentalSubjectList);
            taskTemplateDto.setLabProcId(taskTemplateEMdProc==null?null:taskTemplateEMdProc.getProcId());
            if(details != null){
                taskTemplateDto.setTaskDetails(details.getName());
            }
            if(taskMdDoc != null){
                taskTemplateDto.setTaskMdDoc(taskMdDoc.getMdDocId());
                MDChapter mdChapter = markdownService.getChapterById(taskMdDoc.getMdDocId());
                taskTemplateDto.setMdChapter(mdChapter);
            }
            if(labProc != null){
                taskTemplateDto.setLabProc(labProc);
                taskTemplateDto.setTaskEMdProc(labProc.getId());
            }
        }
        return contentTaskTemplates;
    }

    @Override
    public void deleteTaskTemplateByID(Integer taskTemplateId, Integer user) {
        this.deleteTaskTemplateItem(taskTemplateId);
        List<Integer> resources = referenceFileMapper.getResourceIdByTaskTemplateId(taskTemplateId);
        for(Integer i:resources){
            resourceService.deleteById(i);
        }
        referenceFileMapper.deleteByTaskTemplateId(taskTemplateId);
        taskTemplateMapper.deleteTaskTemplateById(taskTemplateId);
    }

    public void deleteTaskTemplateItem(Integer taskTemplateId){
        List<Integer> backDrops = backDropMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> requirements = requirementMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> deliverableRequirements = deliverableRequirementMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> referenceLinks = referenceLinkMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> taskAttentions = taskAttentionMapper.getAttentionIdByTaskTemplateId(taskTemplateId);
        List<Integer> experimentalSubjects = experimentalSubjectMapper.getExperimentalSubjectIdByTaskTemplateId(taskTemplateId);
        List<Integer> taskDetails = taskDetailsMapper.getDetailsIdByTaskTemplateId(taskTemplateId);
        for(Integer i: backDrops){
            backDropMapper.deleteEntityById(i);
        }
        for(Integer i: requirements){
            requirementMapper.deleteEntityById(i);
        }
        for(Integer i:deliverableRequirements){
            deliverableRequirementMapper.deleteEntityById(i);
        }
        for(Integer i:referenceLinks){
            referenceLinkMapper.deleteEntityById(i);
        }
        for(Integer i:taskAttentions){
            taskAttentionMapper.deleteAttentionById(i);
        }
        for(Integer i:experimentalSubjects){
            experimentalSubjectMapper.deleteExperimentalSubjectById(i);
        }
        for(Integer i: taskDetails){
            taskDetailsMapper.deleteDetailsById(i);
        }
        backDropMapper.deleteByTaskTemplateId(taskTemplateId);
        requirementMapper.deleteByTaskTemplateId(taskTemplateId);
        deliverableRequirementMapper.deleteByTaskTemplateId(taskTemplateId);
        referenceLinkMapper.deleteByTaskTemplateId(taskTemplateId);
        taskAttentionMapper.deleteByTaskTemplateId(taskTemplateId);
        experimentalSubjectMapper.deleteByTaskTemplateId(taskTemplateId);
        taskDetailsMapper.deleteByTaskTemplateId(taskTemplateId);

        taskMdDocMapper.deleteByTaskTemplateId(taskTemplateId);
        taskEMdProcMapper.deleteLabProcByTaskTemplateId(taskTemplateId);
    }

    @Override
    public void addResourceToTaskTemplate(Integer taskTemplateId, Resource resource) {
        TaskReferenceFile taskReferenceFile= new TaskReferenceFile();
        taskReferenceFile.setTaskTemplateId(taskTemplateId);
        taskReferenceFile.setReferenceFileId(resource.getId());
        Integer co = referenceFileMapper.connect(taskReferenceFile);
        if(co != 1){
            throw new InsertException("插入数据异常");
        }
    }


    protected enum taskCover {
        one(1, "0535d42f6bfb410a99cb1969d4917b97.png"),
        two(2, "e6fd432374d147e39bcce187e72bf27c.png"),
        three(3, "efdec9fe617d42ebb9d32097f1cc0f7d.png"),
        four(4, "8063c692d46b410a9577d5b4d995e015.png"),
        five(5, "093beab33bc841f79107bbcc356faf4e.png"),
        six(6, "4a8c37ebeeda439181be0b1a8114605f.png"),
        seven(7, "3230985aec1a4083add7c32ac2af3b29.png"),
        eight(8, "81abcde2238d41ca84efa155b0f82b75.png"),
        nine(9, "7dc8354ea0694acd9e3170d8cf3eb877.png"),
        ten(10, "81e8e0f305a44eb2ac475ca698f3210c.png"),
        eleven(11, "2fa494fea68446a980ce3bf5238a4328.png"),
        twelve(12, "44571326bd294b40b04bcedf2b91e353.png"),
        thirteen(13, "0a8a1c649599412fb6f038b2968fe5fc.png"),
        fourteen(14, "6404934757b144a4bcbbf3db496870bb.png"),
        fifteen(15, "ac44237f27ff46a383c5147a5ffe1c7c.png"),
        sixteen(16, "3e243ad85fbb43b7919db89875f73393.png"),
        seventeen(17, "27f9386a290e4848b8b8d18f064b4fa5.png"),
        eighteen(18, "2f7d365e791a423494f8c8bdcef10fa5.png"),
        nineteen(19, "8227027ecbfc4698b9a6c3157d4dbdec.png"),
        twenty(20, "f5a997440f7e480295a8bc1f9add599e.png"),
        twentyOne(21, "bfbb3c5f9aab403bbb2bbc57638c8e41.png"),
        twentyTwo(22, "15df924401d94281baddcb3963e3964f.png"),
        twentyThree(23, "68c32f0331c043989505cd3f7b4c82ba.png"),
        twentyFour(24, "0710dbea3bab4e5d8c9868797c878026.png"),
        twentyFive(25, "21ed590c42c8477096211e9571c0a46a.png"),
        twentySix(26, "2de8b32f8e3545ea848b06a2dd5f6e80.png"),
        twentySeven(27, "584c7cfc96524fc7baef589e31e57527.png"),
        twentyEight(28, "8b4f9311401946339e5a11640892d562.png"),
        twentyNine(29, "a3c44461076644f894816eb345695eaa.png"),
        thirty(30, "042ddfa1ceb1483b805521949fc71ff7.png");


        private final Integer value;
        private final String filed;

        private taskCover(Integer value, String filed) {
            this.value = value;
            this.filed = filed;
        }

        public Integer getValue() {
            return value;
        }

        public String getFiled() {
            return filed;
        }

        public static taskCover codeOf(int code) {
            for (taskCover prizes : values()) {
                if (prizes.getValue() == code) {
                    return prizes;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
