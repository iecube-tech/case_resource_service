package com.iecube.community.model.tcpClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OnlineBoxResponse {
    @JsonProperty("res")
    Integer res; // 0/1 失败、成功
    @JsonProperty("deviceId")
    Integer deviceId;
    @JsonProperty("deviceState")
    Integer deviceState; // 0 离线  1 在线
    @JsonProperty("pid")
    Integer pid;
    @JsonProperty("idList")
    List<Integer> idList;
    @JsonProperty("ipList")
    List<String> ipList;
    @JsonProperty("strData")
    String strData;
}
