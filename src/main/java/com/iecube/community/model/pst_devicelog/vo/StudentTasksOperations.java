package com.iecube.community.model.pst_devicelog.vo;

import com.iecube.community.model.pst_devicelog.dto.PSTOperations;
import lombok.Data;

import java.util.List;

@Data
public class StudentTasksOperations {
    Integer id;
    String studentName;
    String studentId;
    List<PSTOperations> tasksOperations;
}
