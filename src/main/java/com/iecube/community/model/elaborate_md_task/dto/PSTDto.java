package com.iecube.community.model.elaborate_md_task.dto;

import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PSTDto extends EMDStudentTask {
    private Double weighting;
}
