package com.iecube.community.model.video.service.Impl;

import com.iecube.community.model.resource.service.ex.FileCreateFailedException;
import com.iecube.community.model.resource.service.ex.FileEmptyException;
import com.iecube.community.model.resource.service.ex.FileSizeException;
import com.iecube.community.model.resource.service.ex.FileTypeException;
import com.iecube.community.model.video.entity.Video;
import com.iecube.community.model.video.mapper.VideoMapper;
import com.iecube.community.model.video.service.VideoService;
import com.iecube.community.model.video.service.ex.FFmpegUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class VideoServiceImpl implements VideoService {
    @Value("${resource-location}/video/original/")
    private String originalDir;

    @Value("${resource-location}/video/m3u8/")
    private String m3u8Dir;

    @Value("${FFmpeg.path}")
    private String FFmpegPath;

    @Autowired
    private VideoMapper videoMapper;

    private static final int VIDEO_MAX_SIZE= 1024 * 1024 * 1024;

    private static final List<String> VIDEO_TYPE=new ArrayList<>();
    static {
        VIDEO_TYPE.add("video/mp4");
    }

    @Override
    public Video uploadVideo(MultipartFile file, Integer creator, String name, Integer cover, Integer caseId)  throws IOException {
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
        String originalFilename = saveOriginalVideo(file);
        String filename = convertToM3U8(originalFilename);
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
        videoMapper.uploadVideo(video);
        return video;
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

    public String convertToM3U8(String originalFilename){
        String filename = originalFilename.substring(0, originalFilename.indexOf("."));
        File file = new File(originalDir, originalFilename+".m3u8");
        if(!file.exists()){
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
        try{
            FFmpeg ffmpeg = new FFmpeg(FFmpegPath); // 实例化FFmpeg对象
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(originalDir+originalFilename) // 输入原视频文件路径
                    .addOutput(m3u8Dir+filename+".m3u8")
                    .setFormat("hls") // 设置输出格式为HLS
                    .addExtraArgs("-hls_time", "10") // 设置每个分片的时长
                    .addExtraArgs("-hls_list_size", "0") // 设置m3u8列表大小，0表示无限制
                    .done();
            ffmpeg.run(builder); // 运行转换任务
            return filename;
        } catch (IOException e){
            e.printStackTrace();
            throw new FFmpegUseException("视频转码失败");
        }
    }
}
