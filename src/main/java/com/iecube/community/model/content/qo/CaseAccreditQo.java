package com.iecube.community.model.content.qo;

import lombok.Data;

import java.util.List;

@Data
public class CaseAccreditQo {
    Integer teacherId;
    List<Integer> contentIds;
}
