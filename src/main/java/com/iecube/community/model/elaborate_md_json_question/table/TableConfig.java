package com.iecube.community.model.elaborate_md_json_question.table;

import lombok.Data;

import java.util.List;

@Data
public class TableConfig {
    private int col;
    private int row;
    private String tableName;
    private List<ThCell> tableHeader;
    private List<List<Cell>> tableData;
}
