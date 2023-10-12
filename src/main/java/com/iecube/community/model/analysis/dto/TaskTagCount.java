package com.iecube.community.model.analysis.dto;

import com.iecube.community.util.ListCounter;
import lombok.Data;

import java.util.List;

@Data
public class TaskTagCount {
    Integer taskNum;
    List<ListCounter.Occurrence> tagsCount;
}
