package com.iecube.community.model.websocket.message;

import lombok.Data;

@Data
public class Message3835 {
    String from;
    Boolean isConnecting;
    Integer userId;
    Integer projectId;
    Integer taskNum;
    Integer pstId;
    String snId;
    Boolean lock;
}
