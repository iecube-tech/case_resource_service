package com.iecube.community.util.ffmpegUtil;

import com.iecube.community.model.video.entity.Video;
import com.iecube.community.model.video.mapper.VideoMapper;
import com.iecube.community.model.video.service.ex.FFmpegUseException;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class SaveToM3u8 {
    @Value("${resource-location}/video/original/")
    private String originalDir;

    @Value("${resource-location}/video/m3u8/")
    private String m3u8Dir;

    @Value("${FFmpeg.path}")
    private String FFmpegPath;

    @Autowired
    private VideoMapper videoMapper;

    @Async
    public void convertToM3U8( Video video){
        log.info("开始转码：{}",video.getOriginalFileName());
        String originalFilename = video.getOriginalFileName();
        String filename = video.getFilename();
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
            video.setIsReady(1);
            videoMapper.updateReadyStatus(video);
            log.info(video.getFilename()+"转码成功");
        } catch (IOException e){
            log.error(video.getFilename()+"转码失败");
            e.printStackTrace();
            throw new FFmpegUseException("视频转码失败");
        }
    }
}
