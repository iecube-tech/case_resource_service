package com.iecube.community.model.elaborate_md_task.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetGradeQo {
    @JsonProperty("pstId")
    private Long pstId;
    private Double grade;
}
