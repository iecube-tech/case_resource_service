package com.iecube.community.model.major.vo;

import com.iecube.community.model.major.entity.Collage;
import lombok.Data;

import java.util.List;

@Data
public class CollageListOfSchool {
    Integer id;
    String name;
    List<Collage> collageList;
}
