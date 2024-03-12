package com.iecube.community.model.video.mapper;

import com.iecube.community.model.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper {
    Integer uploadVideo(Video video);
}
