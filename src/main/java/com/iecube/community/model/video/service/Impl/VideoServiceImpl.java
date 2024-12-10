package com.iecube.community.model.video.service.Impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.resource.service.ex.FileCreateFailedException;
import com.iecube.community.model.resource.service.ex.FileEmptyException;
import com.iecube.community.model.resource.service.ex.FileSizeException;
import com.iecube.community.model.resource.service.ex.FileTypeException;
import com.iecube.community.model.video.entity.Video;
import com.iecube.community.model.video.mapper.VideoMapper;
import com.iecube.community.model.video.service.VideoService;
import com.iecube.community.model.video.service.ex.FFmpegUseException;
import com.iecube.community.util.ffmpegUtil.SaveToM3u8;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {
    @Value("${resource-location}/video/original/")
    private String originalDir;
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private SaveToM3u8 saveToM3u8;

    private static final int VIDEO_MAX_SIZE= 1024 * 1024 * 1024;

    private static final List<String> VIDEO_TYPE=new ArrayList<>();
    static {
        VIDEO_TYPE.add("video/mp4");
    }

    @Override
    public Video getByCaseId(Integer caseId){
        Video video = videoMapper.getVideoByCaseId(caseId);
        return video;
    }

    @Override
    public Video getByFilename(String filename) {
        return videoMapper.getVideoByFilename(filename);
    }

    @Override
    public void deleteVideo(Integer id) {
        videoMapper.deleteVideoById(id);
        // todo 删除文件
    }

    @Override
    public Video uploadVideo(MultipartFile file, Integer creator, Integer cover, Integer caseId)  throws IOException {
        if(file==null){
            throw new FileEmptyException("文件为空");
        }
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>VIDEO_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        String originalFileType = file.getContentType();
        if (!VIDEO_TYPE.contains(originalFileType)){
            throw new FileTypeException("不支持的文件格式");
        }
        Video oldVideo = videoMapper.getVideoByCaseId(caseId);
        if(oldVideo != null){
            videoMapper.deleteVideoById(oldVideo.getId());
        }
        String name = file.getOriginalFilename();
        String originalFilename = saveOriginalVideo(file);
        String filename = originalFilename.substring(0, originalFilename.indexOf("."));
        Video video = new Video();
        video.setName(name);
        video.setCover(cover);
        video.setFilename(filename);
        video.setCaseId(caseId);
        video.setOriginalFileName(originalFilename);
        video.setOriginalFileType(originalFileType);
        video.setCreateTime(new Date());
        video.setLastModifiedTime(new Date());
        video.setCreator(creator);
        video.setLastModifiedUser(creator);
        int res = videoMapper.uploadVideo(video);
        if(res!=1){
            throw new InsertException("新增数据失败");
        }
        this.convertToM3U8(video);
        return video;
    }

    @Override
    public Video uploadWithoutCaseId(MultipartFile file, Integer creator) {
        if(file==null){
            throw new FileEmptyException("文件为空");
        }
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>VIDEO_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        String originalFileType = file.getContentType();
        if (!VIDEO_TYPE.contains(originalFileType)){
            throw new FileTypeException("不支持的文件格式");
        }
        try{
            String name = file.getOriginalFilename();
            String originalFilename = saveOriginalVideo(file);
            String filename = originalFilename.substring(0, originalFilename.indexOf("."));
            Video video = new Video();
            video.setName(name);
            video.setCover(null);
            video.setFilename(filename);
            video.setCaseId(null);
            video.setOriginalFileName(originalFilename);
            video.setOriginalFileType(originalFileType);
            video.setCreateTime(new Date());
            video.setLastModifiedTime(new Date());
            video.setCreator(creator);
            video.setLastModifiedUser(creator);
            int res = videoMapper.uploadVideo(video);
            if(res!=1){
                throw new InsertException("新增数据失败");
            }
            this.convertToM3U8(video);
            return video;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    public String saveOriginalVideo(MultipartFile file) throws IOException{
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String name = UUID.randomUUID().toString().replace("-", "")+"."+suffix;
        File target = new File(this.originalDir,name);
        if(!target.exists()){
            File parentFile = target.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            boolean success = target.createNewFile();
            if (!success){
                throw new FileCreateFailedException("创建文件失败");
            }
        }
        FileCopyUtils.copy(file.getBytes(), target);
        return name;
    }

    @Async
    public void convertToM3U8(Video video){
        saveToM3u8.convertToM3U8(video);
    }
}
