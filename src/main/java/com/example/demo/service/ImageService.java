package com.example.demo.service;

import com.example.demo.entity.ImageInfo;
import com.example.demo.repository.ImageRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Slf4j
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    @Transactional
    public ImageInfo saveImageInfo(Path path, String hash) throws IOException {
        Optional<ImageInfo> imageAlreadyExist = imageRepository.findByPathToFileIs(path.toString());
        ImageInfo imageInfo = imageAlreadyExist.orElse(createNewImageInfo(path, hash));
        return imageRepository.saveAndFlush(imageInfo);
    }

    private ImageInfo createNewImageInfo(Path path, String hash) throws IOException {
        File image = path.toFile();
        BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
        BufferedImage bufferedImage = ImageIO.read(image);
        ImageInfo newImageInfo = new ImageInfo();
        newImageInfo.setSize(fileAttributes.size());
        newImageInfo.setFileName(image.getName());
        newImageInfo.setEmailHash(hash);
        newImageInfo.setUploadTimeStamp(LocalDateTime.now());
        newImageInfo.setUpdateTimeStamp(LocalDateTime.now());
        newImageInfo.setHeight(bufferedImage.getHeight());
        newImageInfo.setWidth(bufferedImage.getWidth());
        newImageInfo.setPathToFile(path.toString());
        return newImageInfo;
    }

    public ImageInfo updateImageInfo(ImageInfo imageInfo) {
        throw new NotImplementedException();
    }


}
