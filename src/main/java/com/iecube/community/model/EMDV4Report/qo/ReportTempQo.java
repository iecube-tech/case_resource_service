package com.iecube.community.model.EMDV4Report.qo;

import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import lombok.Data;

import java.util.List;

@Data
public class ReportTempQo {
    private Long id;
    private Integer projectId;
    private String name;
    private List<Chapter> chapterList;

    @Data
    public static class Chapter{
        private ReportTempChapter.ChapterType type;
        private String title;
        private Integer order;
        private Boolean required;
    }
}
