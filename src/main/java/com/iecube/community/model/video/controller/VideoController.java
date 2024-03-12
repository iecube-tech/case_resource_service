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

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController extends VideoBaseController {
    @Value("${resource-location}/video/m3u8/")
    private String m3u8Dir;

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public JsonResult<Video> uploadVideo(MultipartFile file, HttpSession session, Integer caseId,
                                         Integer cover, String name) throws IOException {
        Integer creator = getUserIdFromSession(session);
        Video video = videoService.uploadVideo(file,creator,name, cover, caseId);
        return new JsonResult<>(OK, video);
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
