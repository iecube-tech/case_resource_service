package com.iecube.community.model.EMDV4.Tag.qo;

import lombok.Data;

@Data
public class BLTTagQo {
    private Long id;
    private Long bookId;
    private Long targetId;
    private String name;
    private String ability;
    private String description;
    private String style;
    private String config;
    private String payload;
}
