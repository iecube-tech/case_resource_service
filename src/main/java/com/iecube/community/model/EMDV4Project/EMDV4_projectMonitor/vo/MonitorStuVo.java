package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.MonitorStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MonitorStuVo {
    private Long psId;
    private String stuId;
    private String stuName;
    private Date lastOperateTime;
    private List<PSTInfo> taskList;

    @Data
    public static class PSTInfo{
        private Long ptId;
        private String ptName;
        private MonitorStatus status;
        private Double ptScore;
        private Double ptTotalScore;
        private List<Block> stageList;
    }

    @Data
    public static class Block{
        private Integer blockOrder;
        private String blockName;
        private Date blockStartTime;
        private Date blockEndTime;
        private Integer minutesDiff;
        private MonitorStatus status;
        private List<Block> blockList;
    }
}
