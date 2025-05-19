package com.iecube.community.model.elaborate_md_json_question.qo;

import lombok.Data;

@Data
public class PayloadQo {
    private Long id;
    private Long parentId;
    private String payload;
}
