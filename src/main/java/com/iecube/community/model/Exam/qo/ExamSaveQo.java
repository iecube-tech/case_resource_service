package com.iecube.community.model.Exam.qo;

import lombok.Data;

@Data
public class ExamSaveQo {
    private Integer projectId;
    private String parseId;
    private boolean useRandomOption;
    private boolean useRandomQuestion;
    private boolean aiAutoCheck;
}
