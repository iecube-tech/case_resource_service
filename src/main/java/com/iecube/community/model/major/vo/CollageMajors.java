package com.iecube.community.model.major.vo;

import com.iecube.community.model.major.entity.Major;
import lombok.Data;

import java.util.List;

@Data
public class CollageMajors {
    Integer collageId;
    String collageName;
    Integer schoolId;
    List<Major> majorList;
}
