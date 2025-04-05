package com.iecube.community.model.AI.aiClient.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Payload {
    private String book_id;
    private List<String> image_dataurls;
    private String section_prefix;
    private String question;

    // 构造函数、getter 和 setter 方法
    public Payload(String book_id, List<String> image_dataurls, String section_prefix, String question) {
        this.book_id = book_id;
        this.image_dataurls = image_dataurls;
        this.section_prefix = section_prefix;
        this.question = question;
    }
}


