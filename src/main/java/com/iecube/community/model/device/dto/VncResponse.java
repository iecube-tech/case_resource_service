package com.iecube.community.model.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VncResponse {
    @JsonProperty("res")
    Integer res;
    @JsonProperty("data")
    Integer data;
}
