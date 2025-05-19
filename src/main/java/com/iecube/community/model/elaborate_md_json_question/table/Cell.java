package com.iecube.community.model.elaborate_md_json_question.table;

import lombok.Data;

@Data
public class Cell<T> {
    private String value;
    private boolean isNeedInput;
    private boolean isAutoGet;
    private T stuValue;
}
