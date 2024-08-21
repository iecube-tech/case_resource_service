package com.iecube.community.model.task.entity;

import com.iecube.community.model.pst_article.entity.PSTArticle;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.entity.ResourceVo;
import com.iecube.community.model.tag.entity.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentTaskDetailVo {
    Integer PSTId;
    Integer studentId;
    Integer projectId;
    Integer taskNum;
    Double weighting;
    Double classHour;
    String taskName;
    Double taskGrade;
    String dataTables;
    List<Tag> taskTags;
    String taskEvaluate;
    String taskImprovement;
    String taskContent;
    Integer taskStatus;
    List<PSTResourceVo> resources;
    Integer taskResubmit;
    Date taskStartTime;
    Date taskEndTime;
    Integer questionListSize;
//    PSTMarkdownOperate pstMarkdownOperate;
    PSTArticle pstArticle;
}
