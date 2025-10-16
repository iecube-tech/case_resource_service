package com.iecube.community.model.taskTemplate.dto;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.task_attention.entity.Attention;
import com.iecube.community.model.task_back_drop.entity.BackDrop;
import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_experimental_subject.entity.ExperimentalSubject;
import com.iecube.community.model.task_reference_file.entity.TaskReferenceFile;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_requirement.entity.Requirement;
import lombok.Data;

import java.util.List;

@Data
public class TaskTemplateDto {
    private Integer id;
    private Integer contentId;
    private Integer num; //用于一个项目下的任务排序
    private Double weighting;
    private Double classHour;
    private String taskName;
    private String taskCover;
    private Integer taskDevice;
    private List<BackDrop> backDropList;
    private List<Requirement> requirementList;
    private List<DeliverableRequirement> deliverableRequirementList;
    private List<ReferenceLink> referenceLinkList;
    private List<Resource> referenceFileList;
    private List<Attention> attentionList;
    private List<ExperimentalSubject> experimentalSubjectList;
    private String taskDetails;
    private Integer taskMdDoc;
    private MDChapter mdChapter;
    private Long taskEMdProc;
    private LabProc labProc;
    private Long labProcId;
    private Boolean step1NeedPassScore;
    private Double step1PassScore;  // 课前预习通过分数的阈值
    private Integer version;
    private Boolean useCoder;
    private String coderType;
}
