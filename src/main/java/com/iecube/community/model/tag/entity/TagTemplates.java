package com.iecube.community.model.tag.entity;

import lombok.Data;

@Data
public class TagTemplates {
    Integer id;
    String name;
    String suggestion;
    Integer caseId;
    Integer taskNum;
}
