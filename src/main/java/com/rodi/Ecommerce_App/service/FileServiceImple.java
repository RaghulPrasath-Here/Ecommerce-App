package com.rodi.Ecommerce_App.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FileServiceImple implements FileService{
    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();

        String random = UUID.randomUUID().toString();
        String fileName = random.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);

        if(!folder.exists()){
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
