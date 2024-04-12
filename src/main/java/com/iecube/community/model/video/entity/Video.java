package com.iecube.community.model.video.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Video extends BaseEntity {
    Integer Id;
    Integer caseId;
    Integer cover; // resourceId
    String name;
    String filename; //.m3u8
    String originalFileName;
    String originalFileType;
    Integer isReady;
}
