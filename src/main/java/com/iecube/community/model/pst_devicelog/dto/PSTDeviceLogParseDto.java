package com.iecube.community.model.pst_devicelog.dto;

import lombok.Data;

@Data
public class PSTDeviceLogParseDto {
    Integer id;
    Integer pstId;
    String categories;
    String data;
    String times;
    String operations;
}
