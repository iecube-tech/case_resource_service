package com.iecube.community.model.teacher.vo;

import com.iecube.community.model.content.entity.Content;
import lombok.Data;

import java.util.List;

@Data
public class TeacherVo {
    Integer teacherId;
    String teacherName;
    String teacherEmail;
    Integer isDelete;
    Integer schoolId;
    String schoolName;
    Integer collageId;
    String collageName;
    List<Content> caseList;
    String creatorName;
    Integer creatorId;
}
