package com.iecube.community.model.resource.service;

import com.iecube.community.model.resource.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    Resource UploadReadOverPDF(MultipartFile file, String originalFilename, Integer creator) throws  IOException;

    Resource UploadImage(MultipartFile file, Integer lastModifiedUser) throws IOException;
    Resource UploadFile(MultipartFile file, Integer creator) throws IOException;
    void deleteResource(String filename);

    void deleteById(Integer id);

    Resource getResourceByFilename(String filename);

    Resource getResourceById(Integer id);

    Resource buildResourceDTO(String originalFilename, String fileName, String type);

    Resource addResource(Resource resource, Integer createUser);
}
