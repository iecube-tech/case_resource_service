package com.iecube.community.model.Ai2830.dto;

import lombok.Data;

import java.util.List;

@Data
public class StreamAi2830Msg {
    private String type;  // current message-ack activity-start stream message activity-stop
    private AI2830Msg message;
    private List msgList;
}
