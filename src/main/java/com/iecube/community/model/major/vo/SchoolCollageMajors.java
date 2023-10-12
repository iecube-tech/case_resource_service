package com.iecube.community.model.major.vo;

import lombok.Data;

import java.util.List;

@Data
public class SchoolCollageMajors {
    Integer schoolId;
    String schoolName;
    List<CollageMajors> collageMajorsList;
}
