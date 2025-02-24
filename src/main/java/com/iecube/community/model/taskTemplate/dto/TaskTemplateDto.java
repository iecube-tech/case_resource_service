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
    Integer id;
    Integer contentId;
    Integer num; //用于一个项目下的任务排序
    Double weighting;
    Double classHour;
    String taskName;
    String taskCover;
    Integer taskDevice;
    List<BackDrop> backDropList;
    List<Requirement> requirementList;
    List<DeliverableRequirement> deliverableRequirementList;
    List<ReferenceLink> referenceLinkList;
    List<Resource> referenceFileList;
    List<Attention> attentionList;
    List<ExperimentalSubject> experimentalSubjectList;
    String taskDetails;
    Integer taskMdDoc;
    MDChapter mdChapter;
    Long taskEMdProc;
    LabProc labProc;
}
