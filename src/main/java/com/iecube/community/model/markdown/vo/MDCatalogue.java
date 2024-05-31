package com.iecube.community.model.markdown.vo;

import com.iecube.community.model.markdown.entity.MDChapter;
import lombok.Data;

import java.util.List;

@Data
public class MDCatalogue {
    Integer courseId;
    String courseName;
    List<MDChapter> chapterList;
}
