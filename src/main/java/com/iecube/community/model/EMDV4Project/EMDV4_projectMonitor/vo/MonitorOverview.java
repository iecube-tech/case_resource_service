package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.MonitorStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MonitorOverview {
    private taskInfo task;
    private List<taskInfo> tasks;
    private List<monitorOverview> stuMonitors;
    private int total;

    @Data
    public static class monitorOverview {
        private Long psId;
        private String stuId;
        private String stuName;
        private Double psScore;
        private MonitorStatus status;
        private List<taskInfo> tasks;
        private taskInfo pstInfo;
    }

    @Data
    public static class taskInfo{
        private Long pstId;
        private Long ptId;
        private String ptName;
        private Double ptScore;
        private Double ptTotalScore;
        private MonitorStatus status;
        private List<taskStage> stageList;
    }

    @Data
    public static class taskStage{
        private Integer stage;
        private String stageName;
        private MonitorStatus stageStatus;
        private List<stageBlock> stageBlockList;
    }

    @Data
    public static class stageBlock{
        private Integer blockOrder;
        private String blockName;
        private Date blockStartTime;
        private Date blockEndTime;
        private Integer minutesDiff;
        private MonitorStatus status;
    }


}
