package com.example.demo.service;

import com.example.demo.entity.ImageInfo;
import com.example.demo.repository.ImageRepository;
import com.example.demo.storage.StorageService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Slf4j
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final StorageService storageService;

    public ImageService(ImageRepository imageRepository, StorageService storageService) {
        this.imageRepository = imageRepository;
        this.storageService = storageService;
    }


    @Transactional
    public ImageInfo saveImageInfo(Path path) throws IOException {
        ImageInfo newImageInfo = new ImageInfo();
        File image = path.toFile();
        BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
        newImageInfo.setSize(fileAttributes.size());
        newImageInfo.setUploadTimeStamp(LocalDateTime.now());
        newImageInfo.setUpdateTimeStamp(LocalDateTime.now());
        BufferedImage bufferedImage = ImageIO.read(image);
        newImageInfo.setHeight(bufferedImage.getHeight());
        newImageInfo.setWidth(bufferedImage.getWidth());
        newImageInfo.setPathToFile(path.toString());
        return imageRepository.saveAndFlush(newImageInfo);
    }

    public ImageInfo updateImageInfo(ImageInfo imageInfo) {
        throw new NotImplementedException();
    }


}
