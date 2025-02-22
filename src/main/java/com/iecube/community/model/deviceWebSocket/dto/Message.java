package com.iecube.community.model.deviceWebSocket.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class Message {
    private String type;
    private JsonNode data;
}
