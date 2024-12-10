package com.iecube.community.model.teacher.vo;

import com.iecube.community.model.content.entity.Content;
import lombok.Data;

import java.util.List;

@Data
public class TeacherVo {
    private Integer teacherId;
    private String teacherName;
    private String teacherEmail;
    private Integer isDelete;
    private Integer schoolId;
    private String schoolName;
    private Integer collageId;
    private String collageName;
    private List<Content> caseList;
    private String creatorName;
    private Integer creatorId;
}
