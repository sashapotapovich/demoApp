package com.example.demo.repository;

import com.example.demo.entity.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageInfo, Long> {
}
