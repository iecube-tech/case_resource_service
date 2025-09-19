package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.qo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class StepWeightingQo {
    private List<StepWeighting> stepWeightings;
    private Long pstId;
    private String taskBookId;
    private Double totalWeight;

    @Setter
    @Getter
    public static class StepWeighting {
        private String blockId;
        private String name;
        private Double weighting;
    }
}
