package com.iecube.community.model.analysis.vo;

import lombok.Data;

import java.util.List;

@Data
public class CaseAnalysis {
    Integer usedTime;
    Integer numberOfParticipant;
    ScoreDistributionHistogram scoreDistributionHistogram;
    List<ScoreDistributionHistogram> caseTaskScoreDistributionHistogram;
    List<TagCountVo> tagCounterOfCase;
}
