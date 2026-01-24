package com.iecube.community.model.Exam.vo;

import com.iecube.community.model.Exam.Dto.QuesType;
import com.iecube.community.model.Exam.entity.ExamInfoEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExamParseVo {

    private String id;
    private ExamInfoEntity exam;
    private Map<QuesType, List<QuesVo>> questions;

    @Data
    public static class QuesVo<T>{
        private String id;
        private QuesType quesType;
        private Integer order;
        private Boolean isRandom;  // true->T=List<QuestionContent>  //false -> T = QuestionContent
        private Integer randomNum;
        private Integer difficulty;
        private Double score;
        private T quesContent;
    }
}
