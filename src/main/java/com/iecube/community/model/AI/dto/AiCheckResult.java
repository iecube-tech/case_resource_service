package com.iecube.community.model.AI.dto;

import lombok.Data;

@Data
public class AiCheckResult {
    private Long id;
    private Integer studentId;
    private Integer taskId;
    private String result;
}
