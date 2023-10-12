package com.iecube.community.model.major.vo;

import com.iecube.community.model.major.entity.ClassAndGrade;
import lombok.Data;

import java.util.List;

@Data
public class MajorClass {
    Integer majorId;
    String majorName;
    List<ClassAndGrade> majorClasses;
}
