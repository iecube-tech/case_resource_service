package com.iecube.community.ys;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class YsModel {

    public static final String SUCCESS_CODE = "200";

    private String appKey;
    private String appSecret;
    private String msg;
    private String code;
    private JsonNode data;

}
