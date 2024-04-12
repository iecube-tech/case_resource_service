package com.iecube.community.model.video.service;

import com.iecube.community.model.video.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoService {

    Video uploadVideo(MultipartFile file, Integer creator, Integer cover, Integer caseId) throws IOException;

    Video getByCaseId(Integer caseId);

    void deleteVideo(Integer id);
}
