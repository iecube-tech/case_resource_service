package com.iecube.community.model.resource.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.resource.service.ex.*;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.resource.service.ResourceService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
    // 图片路径
    @Value("${resource-location}/image")
    private String image;

    // 文件路径
    @Value("${resource-location}/file")
    private String files;

    // 上传图片的最大值
    private static final int IMAGE_MAX_SIZE=10 * 1024 * 1024;
    // 上传文件的最大值
    private static final int FILE_MAX_SIZE= 1024 * 1024 * 1024;

    // 支持的图片类型
    private static final List<String> IMAGE_TYPE=new ArrayList<>();
    static {
        IMAGE_TYPE.add("image/jpeg");
        IMAGE_TYPE.add("image/png");
        IMAGE_TYPE.add("image/bmp");
        IMAGE_TYPE.add("image/gif");
    }

    private File target;

    private Resource buildResourceDTO(String originalFilename, String fileName, String type) {
        Resource resource = new Resource();
        resource.setOriginFilename(originalFilename);
        resource.setFilename(fileName);
        resource.setName(fileName+originalFilename);
        resource.setType(type);
        return resource;
    }

    private Resource addResource(Resource resource, Integer createUser) {
        resource.setCreator(createUser);
        resource.setCreateTime(new Date());
        resource.setLastModifiedUser(createUser);
        resource.setLastModifiedTime(new Date());
        Integer rows = resourceMapper.insert(resource);
        if (rows != 1){
            throw new InsertException("插入数据异常");
        }
        return resource;
    }


    private String SaveFile(MultipartFile file, String tag) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String fileName = UUID.randomUUID().toString().replace("-", "")+"."+suffix;
        if (tag.equals("image")){
            this.target = new File(this.image, fileName);
        }else {
            this.target = new File(this.files, fileName);
        }
        if (!target.exists()) {
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
        return fileName;
    }

    /**
     * 只支持blob格式的pdf文件上传
     * @param file
     * @return
     * @throws IOException
     */
    private String SaveBlobFile(MultipartFile file) throws IOException{
        String fileName = UUID.randomUUID().toString().replace("-", "")+".pdf";
        this.target = new File(this.files, fileName);
        if (!target.exists()) {
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
        return fileName;
    }


    @Override
    public Resource UploadReadOverPDF(MultipartFile file,String originalFilename, Integer creator) throws  IOException{
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>FILE_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        String type = file.getContentType();
        String fileName = this.SaveBlobFile(file);
        Resource resource = this.buildResourceDTO(originalFilename, fileName, type);
        Resource result = this.addResource(resource,creator);
        return result;
    }

    @Override
    public Resource UploadImage(MultipartFile file, Integer creator) throws IOException{
//        System.out.println(file);
        if(file==null){
            throw new FileEmptyException("文件为空");
        }
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>IMAGE_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        String imageType = file.getContentType();
//        System.out.println(imageType);
        if (!IMAGE_TYPE.contains(imageType)){
            throw new FileTypeException("不支持的文件格式");
        }
        String originalFilename = file.getOriginalFilename();
        String filename = this.SaveFile(file, "image");
        Resource resource = this.buildResourceDTO(originalFilename, filename, imageType);
        Resource result = this.addResource(resource, creator);
//        System.out.println(result);
        return result;
    }

    @Override
    public Resource UploadFile(MultipartFile file, Integer creator) throws IOException{
        if (file.isEmpty()){
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>FILE_MAX_SIZE){
            throw new FileSizeException("文件大小超出限制");
        }
        String type = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        String fileName = this.SaveFile(file, "file");
        Resource resource = this.buildResourceDTO(originalFilename, fileName, type);
        Resource result = this.addResource(resource,creator);
        return result;
    }


    @Override
    public void deleteResource(String filename) {
        Resource resource = resourceMapper.getByFileName(filename);
        this.deleteResource(resource);
    }

    @Override
    public void deleteById(Integer id){
        Resource resource = resourceMapper.getById(id);
        this.deleteResource(resource);
    }

    private void deleteResource(Resource resource){
        if(resource == null){
            throw new ResourceNotFoundException("文件未找到");
        }
        String fileType = resource.getType();
        File filePath = new File(this.files, resource.getFilename());
        if(fileType.contains("image")){
            filePath = new File(this.image, resource.getFilename());
        }
        log.warn("DELETE:{}",filePath);
        if(!filePath.exists()){
            throw new FileNotFoundException("文件不存在");
        }
        if(!filePath.delete()){
            throw new FileDeletedFailedException("删除文件错误");
        }
        Integer rows = resourceMapper.delete(resource.getId());
        if (rows != 1) {
            throw new DeleteException("未知的删除异常");
        }
    }
}
