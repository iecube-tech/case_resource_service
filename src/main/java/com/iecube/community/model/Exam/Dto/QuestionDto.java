package com.iecube.community.model.Exam.Dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private String id;
    private Long examId;
    private Integer order;
    private QuesType type;
    private String question;
    private JsonNode options;
    private String answer;
    private Double score;
    private Integer difficulty;
    private List<String> knowledge;
    private Boolean isRandom;
    private Integer randomType;
    private Integer randomNumber;
}
