package com.iecube.community.model.EMDV4.BookTarget.vo;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;
import lombok.Data;

import java.util.List;

@Data
public class BookTargetVo {
    private BookLabCatalog book;
    private List<CourseTarget> targetList;
}
