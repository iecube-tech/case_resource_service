package com.iecube.community.model.task.entity;

import com.iecube.community.entity.BaseEntity;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.task_attention.entity.Attention;
import com.iecube.community.model.task_back_drop.entity.BackDrop;
import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_experimental_subject.entity.ExperimentalSubject;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_requirement.entity.Requirement;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Task extends BaseEntity {
    Integer id;
    Integer taskTemplateId;
    Integer projectId;
    Integer num; //用于一个项目下的任务排序
    Double weighting;
    Double classHour;
    String taskName;
    String taskCover;
    Integer taskDevice;
    String taskDataTables;
    List<BackDrop> backDropList;
    List<Requirement> requirementList;
    List<DeliverableRequirement> deliverableRequirementList;
    List<ReferenceLink> referenceLinkList;
    List<Resource> referenceFileList;
    List<Attention> attentionList;
    List<ExperimentalSubject> experimentalSubjectList;
    String taskDetails;
    Date taskStartTime;
    Date taskEndTime;
    Integer taskMdDoc;
    MDChapter mdChapter;
}
