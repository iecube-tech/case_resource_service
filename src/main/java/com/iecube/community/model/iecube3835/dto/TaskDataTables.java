package com.iecube.community.model.iecube3835.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskDataTables {
    String name;
    List params;
    List columnList;
    List rowData;
}
