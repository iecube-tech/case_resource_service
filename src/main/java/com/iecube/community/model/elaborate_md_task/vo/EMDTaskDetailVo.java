package com.iecube.community.model.elaborate_md_task.vo;

import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EMDTaskDetailVo {
    private EMDStudentTask emdStudentTask;
    private List<EMDTaskModelVo> labModelVoList;
}
