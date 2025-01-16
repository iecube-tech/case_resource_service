package com.iecube.community.model.video.service;

import com.iecube.community.model.video.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {

    Video uploadVideo(MultipartFile file, Integer creator, Integer cover, Integer caseId) throws IOException;

    Video uploadWithoutCaseId(MultipartFile file, Integer creator);

    List<Video> uploadVideoWithCaseId(MultipartFile file, Integer creator, Integer caseId);

    List<Video> getVideoListByCaseId(Integer caseId);

    Video getByCaseId(Integer caseId);

    Video getByFilename(String filename);
    
    void deleteVideo(Integer id);
}
