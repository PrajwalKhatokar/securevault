package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.DownloadLog;
import com.prajwal.securevault.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadLogRepository extends JpaRepository<DownloadLog, Long> {
    List<DownloadLog> findByFile(FileEntity file);
}
