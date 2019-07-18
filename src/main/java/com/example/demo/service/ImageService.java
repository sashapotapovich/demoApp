package com.example.demo.service;

import com.example.demo.entity.ImageInfo;
import com.example.demo.repository.ImageRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public void loadImage(String path) throws IOException {
        ImageInfo newImageInfo = new ImageInfo();
        File image = new File(path);
        BasicFileAttributes fileAttributes = Files.readAttributes(image.toPath(), BasicFileAttributes.class);
        newImageInfo.setSize(fileAttributes.size());
        newImageInfo.setUploadTimeStamp(LocalDateTime.now());
        newImageInfo.setUpdateTimeStamp(LocalDateTime.now());
        BufferedImage bufferedImage = ImageIO.read(image);
        newImageInfo.setHeight(bufferedImage.getHeight());
        newImageInfo.setWidth(bufferedImage.getWidth());
        newImageInfo.setPathToFile(path);
        imageRepository.saveAndFlush(newImageInfo);
    }


}
