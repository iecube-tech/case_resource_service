package com.iecube.community.util.LogParsing;

import lombok.Data;

import java.util.List;

@Data
public class DeviceLogData {
    String name;
    List value;
    ItemStyle itemStyle;
}
