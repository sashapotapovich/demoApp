package com.example.demo.repository;

import com.example.demo.entity.ImageInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageInfo, Long> {
    
    Optional<ImageInfo> findByPathToFileIs(String path);
    
    List<ImageInfo> findAllByEmailHash(String hash);
    
    
}
