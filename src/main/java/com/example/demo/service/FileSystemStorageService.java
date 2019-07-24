package com.example.demo.service;

import com.example.demo.exception.StorageException;
import com.example.demo.exception.StorageFileNotFoundException;
import com.example.demo.storage.StorageProperties;
import com.example.demo.storage.StorageService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final UserDetailsServiceImpl userDetails;
    private Path userLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties, UserDetailsServiceImpl userDetails) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.userDetails = userDetails;
        this.userLocation = rootLocation;
    }
    
    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
            throw new StorageException("Could not initialize storage", ex);
        }
    }

    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                           StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new StorageException("Failed to store file " + filename, ex);
        }
    }

    @Override
    public Path store(InputStream inputStream, String fileName) {
        userLocation = rootLocation.resolve(String.valueOf(userDetails.getUser().getEmail().hashCode()));
        try {
            Files.createDirectories(userLocation);
            try (OutputStream outputStream = new FileOutputStream(userLocation.resolve(fileName).toString())) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                outputStream.write(buffer);
                return userLocation.resolve(fileName).toAbsolutePath();
            }
        } catch (IOException e) {
            log.error("Unable to save file, Exception - {}", e.getLocalizedMessage());
        }
        return userLocation;
    }

    @Override
    public List<Path> loadAll() {
        userLocation = rootLocation.resolve(String.valueOf(userDetails.getUser().getEmail().hashCode()));
        try {
            checkFolderExistence(userLocation);
            return Files.walk(this.userLocation, 1)
                        .filter(path -> !path.equals(this.userLocation))
                        .map(Path::toAbsolutePath)
                        .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new StorageException("Failed to read stored files", ex);
        }

    }

    private void checkFolderExistence(Path path) throws IOException {
        if (!userLocation.toFile().exists()){
            Files.createDirectories(path);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, ex);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public boolean deleteFile(Path path) {
        try {
            checkFolderExistence(userLocation);
            log.info("Preparing to delete file - {}", path.toString());
            return FileSystemUtils.deleteRecursively(path.toAbsolutePath());
        } catch (IOException e) {
            log.error("Unable to delete File - {}, Error - {}", path.toString(), e);
        }
        return false;
    }
    
}
