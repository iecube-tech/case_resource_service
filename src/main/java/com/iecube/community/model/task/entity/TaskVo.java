package com.iecube.community.model.task.entity;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
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
    private Integer id;
    private Integer taskTemplateId;
    private Integer projectId;
    private Integer num;
    private Double weighting;
    private Double classHour;
    private String taskName;
    private String taskCover;
    private Integer taskDevice;
    private String taskDataTables;
    private Date taskStartTime;
    private Date taskEndTime;
    private List<BackDrop> backDrops;
    private List<Requirement> taskTargets;
    private List<DeliverableRequirement> taskDeliverables;
    private List<ReferenceLink> taskReferenceLinks;
    private List<Resource> taskReferenceFiles;
    private List<Attention> attentionList;
    private List<ExperimentalSubject> experimentalSubjectList;
    private String taskDetails;
    private Integer taskMdDoc;
    private MDChapter mdChapter;
    private Long taskEMDProc;
    private LabProc labProc;
    private Boolean step1NeedPassScore;
    private Double step1PassScore;
}
