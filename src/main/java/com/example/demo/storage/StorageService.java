package com.example.demo.storage;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    Path store(InputStream inputStream, String fileName);

    List<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);
    
    Path updateFileName(String path, String name);

    void deleteAll();

    boolean deleteFile(Path path);

}
