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
    private Integer PSTId;
    private Integer studentId;
    private Integer projectId;
    private Integer taskNum;
    private Double weighting;
    private Double classHour;
    private String taskName;
    private Double taskGrade;
    private String dataTables;
    private List<Tag> taskTags;
    private String taskEvaluate;
    private String taskImprovement;
    private String taskContent;
    private Integer taskStatus;
    private List<PSTResourceVo> resources;
    private Integer taskResubmit;
    private Date taskStartTime;
    private Date taskEndTime;
    private Integer questionListSize;
//    PSTMarkdownOperate pstMarkdownOperate;
    private PSTArticle pstArticle;
    private Boolean step1NeedPassScore;
    private Double step1PassScore;  // 课前预习通过分数的阈值
    private Integer version;
    private Boolean useCoder;
    private Boolean useLabProc;
    private String coderType;
}
