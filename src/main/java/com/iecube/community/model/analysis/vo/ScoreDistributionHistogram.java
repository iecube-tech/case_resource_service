package com.iecube.community.model.analysis.vo;

import lombok.Data;

import java.util.List;

@Data
public class ScoreDistributionHistogram {
    List<String> x;
    List<Integer> y;
}
