package com.iecube.community.model.resource.controller;

import com.iecube.community.basecontroller.resource.ResourceBaseController;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.resource.vo.WangEditorRes;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class ResourceController extends ResourceBaseController {
    @Autowired
    private ResourceService resourceService;

    @Value("${resource-location}/image")
    private String image;

    // 文件路径
    @Value("${resource-location}/file")
    private String files;
    

    /**
     * 上传图片
     * @param file
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping("/upimage")
    public JsonResult<Resource> UploadImage(MultipartFile file, HttpSession session) throws IOException{
        Integer creator = getUserIdFromSession(session);
        Resource resource = resourceService.UploadImage(file,creator);
        return new JsonResult<>(OK, resource);
    }

    @PostMapping ("/wdupimage")
    public JsonResult<WangEditorRes> WangEditorUploadImage(MultipartFile file) throws IOException{
//        System.out.println(file);
        Integer creator = 0;
        Resource resource = resourceService.UploadImage(file,creator);
        WangEditorRes wangEditorRes = new WangEditorRes();
        wangEditorRes.setUrl("/dev-api/files/image/"+resource.getFilename());
        return new JsonResult<>(OK,0,wangEditorRes);
    }

    @PostMapping ("/e/image")
    public JsonResult<WangEditorRes> EditorUploadImage(MultipartFile file) throws IOException{
//        System.out.println(file);
        Integer creator = 0;
        Resource resource = resourceService.UploadImage(file,creator);
        WangEditorRes wangEditorRes = new WangEditorRes();
        wangEditorRes.setUrl("/files/image/"+resource.getFilename());
        wangEditorRes.setAlt(resource.getOriginFilename());
        wangEditorRes.setTitle(resource.getOriginFilename());
        wangEditorRes.setFilename(resource.getFilename());
        return new JsonResult<>(OK,0,wangEditorRes);
    }

    @PostMapping("/upfile")
    public JsonResult<Resource> UploadFile(MultipartFile file, HttpSession session) throws IOException{
        Integer creator = getUserIdFromSession(session);
        Resource resource = resourceService.UploadFile(file,creator);
        return new JsonResult<>(OK, resource);
    }

//    /**
//     * 上传blob对象的文件， 功能用于教师批改pdf文件，配合PST使用
//     * @param file
//     * @param filename  filename
//     */
//    @PostMapping("/upblob")
//    public JsonResult<Void> UploadBlob(@RequestBody MultipartFile file, String filename, HttpSession session)throws IOException{
//        Integer lastModifiedUser = getUserIdFromSession(session);
//        String fileName = this.SaveBlobFile(file,"file");
//        System.out.println(fileName);
//        return new JsonResult<>(OK);
//    }


    /**
     * 请求图片
     * @param fileName
     * @param response
     */
    @GetMapping("image/{fileName}")
    public void GetImage(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
//        DownloadUtil.httpDownload(new File(this.image, fileName), resource.getOriginFilename(), response);
        DownloadUtil.httpDownload(new File(this.image, fileName), response);
    }

    /**
     * 请求文件
     * @param fileName
     * @param response
     */
    @GetMapping("file/{fileName}")
    public void GetFile(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
        DownloadUtil.httpDownload(new File(this.files, fileName), resource.getOriginFilename(), response);
    }

}
