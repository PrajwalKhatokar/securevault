package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // Existing method to find files by uploader
    List<FileEntity> findByUploadedBy(String username);

    // For files uploaded before a certain time (you might not need this now)
    List<FileEntity> findByUploadedAtBefore(LocalDateTime dateTime);

    // ✅ Add this to find files expired before now (for your scheduler)
    List<FileEntity> findByExpiresAtBefore(LocalDateTime dateTime);


}
