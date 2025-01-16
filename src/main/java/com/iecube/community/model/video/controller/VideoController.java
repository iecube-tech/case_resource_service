package com.iecube.community.model.video.controller;

import com.iecube.community.basecontroller.video.VideoBaseController;
import com.iecube.community.model.video.entity.Video;
import com.iecube.community.model.video.service.VideoService;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController extends VideoBaseController {
    @Value("${resource-location}/video/m3u8/")
    private String m3u8Dir;

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload/{caseId}")
    public JsonResult<Video> uploadVideo(MultipartFile file,@PathVariable Integer caseId,
                                         Integer cover) throws IOException {
        Integer creator = currentUserId();
        Video video = videoService.uploadVideo(file,creator, cover, caseId);
        return new JsonResult<>(OK, video);
    }

    @GetMapping("/c/{caseId}")
    public JsonResult<Video> getByCaseId(@PathVariable Integer caseId){
        Video video = videoService.getByCaseId(caseId);
        return new JsonResult<>(OK, video);
    }

    @DeleteMapping("/d/{id}")
    public JsonResult<Void> deleteVideo(@PathVariable Integer id){
        videoService.deleteVideo(id);
        return new JsonResult<>(OK);
    }

    @PostMapping("/n/up")
    public JsonResult<Video> uploadVideoWithoutCase(MultipartFile file){
        Integer creator = currentUserId();
        Video video = videoService.uploadWithoutCaseId(file, creator);
        return new JsonResult<>(OK, video);
    }

    @PostMapping("/n/up/{caseId}")
    public JsonResult<List<Video>> uploadVideoWithCase(MultipartFile file,@PathVariable Integer caseId){
        Integer creator = currentUserId();
        List<Video> videoList = videoService.uploadVideoWithCaseId(file, creator, caseId);
        return new JsonResult<>(OK,videoList);
    }

    @GetMapping("/n/list/{caseId}")
    public JsonResult<List<Video>> getVideoByCaseId(@PathVariable Integer caseId){
        List<Video> videoList = videoService.getVideoListByCaseId(caseId);
        return new JsonResult<>(OK, videoList);
    }

    @GetMapping("/n/{filename}")
    public JsonResult<Video> getVideoByFilename(@PathVariable String filename){
        Video video = videoService.getByFilename(filename);
        return new JsonResult<>(OK, video);
    }

    @DeleteMapping("/del/{filename}")
    public JsonResult<Void> deleteVideoByFilename(@PathVariable String filename){
        Video video = videoService.getByFilename(filename);
        videoService.deleteVideo(video.getId());
        return new JsonResult<>(OK);
    }
    @GetMapping("/m3u8/{filename}")
    public ResponseEntity<Object>  get3m8uFile(@PathVariable String filename){
        try{
            File m3u8File = new File(m3u8Dir,filename);
            if (m3u8File.exists()) {
                // 读取M3U8文件内容
                FileInputStream fileInputStream = new FileInputStream(m3u8File);
                byte[] data = new byte[(int) m3u8File.length()];
                fileInputStream.read(data);
                fileInputStream.close();

                // 设置响应头为M3U8类型
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("application/vnd.apple.mpegurl"))
                        .body(data);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Object> getTsFile(@PathVariable String filename){
        try{
            File tsFile = new File(m3u8Dir, filename);
            if (tsFile.exists()) {
                // 读取TS文件内容
                FileInputStream fileInputStream = new FileInputStream(tsFile);
                byte[] data = new byte[(int) tsFile.length()];
                fileInputStream.read(data);
                fileInputStream.close();

                // 设置响应头为TS文件类型
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("video/mp2t"))
                        .body(data);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
