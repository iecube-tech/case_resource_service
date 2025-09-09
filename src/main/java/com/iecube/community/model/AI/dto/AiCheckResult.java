package com.iecube.community.model.AI.dto;

import lombok.Data;

@Data
public class AiCheckResult {
    private Long id;
    private Integer studentId;
    private Long taskId;
    private String result;
}
