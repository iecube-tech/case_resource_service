package com.iecube.community.model.task.entity;

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
public class TaskVo {
    Integer id;
    Integer taskTemplateId;
    Integer projectId;
    Integer num;
    Double weighting;
    Double classHour;
    String taskName;
    String taskCover;
    Integer taskDevice;
    String taskDataTables;
    Date taskStartTime;
    Date taskEndTime;
    List<BackDrop> backDrops;
    List<Requirement> taskTargets;
    List<DeliverableRequirement> taskDeliverables;
    List<ReferenceLink> taskReferenceLinks;
    List<Resource> taskReferenceFiles;
    List<Attention> attentionList;
    List<ExperimentalSubject> experimentalSubjectList;
    String taskDetails;
    Integer taskMdDoc;
    MDChapter mdChapter;
}
