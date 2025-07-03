package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUploadedBy(String uploadedBy);
}
