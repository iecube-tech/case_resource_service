package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MonitorInfoVo {
    private Integer id;
    private String projectName;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Integer stuNum;
    private Date lastUpdateTime;
    private List<TaskInfo> taskInfoList;

    @Data
    public static class TaskInfo{
        private String taskName;
        private Boolean isProject;
        private Long ptId;
        private Integer doneNum;
        private Integer doingNum;
        private Integer notStartedNum;
        private Double rageOfDoneNum;
    }
}
