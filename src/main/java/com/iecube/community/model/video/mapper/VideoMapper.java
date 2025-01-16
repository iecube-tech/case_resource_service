package com.iecube.community.model.video.mapper;

import com.iecube.community.model.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoMapper {
    Integer uploadVideo(Video video);

    Video getVideoByCaseId(Integer CaseId);

    Video getVideoByFilename(String Filename);

    Integer deleteVideoById(Integer id);

    Integer updateReadyStatus(Video video);

    List<Video> getVideoListByCaseId(Integer CaseId);
}
