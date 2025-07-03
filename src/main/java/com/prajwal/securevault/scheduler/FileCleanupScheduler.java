package com.prajwal.securevault.scheduler;

import com.prajwal.securevault.entity.FileEntity;
import com.prajwal.securevault.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FileCleanupScheduler {

    @Autowired
    private FileRepository fileRepository;

    // ✅ Runs every minute
    @Scheduled(cron = "0 * * * * *")
    public void cleanExpiredFiles() {
        LocalDateTime now = LocalDateTime.now();

        // ✅ Fetch files whose expiry is before now
        List<FileEntity> expiredFiles = fileRepository.findByExpiresAtBefore(now);

        for (FileEntity file : expiredFiles) {
            File diskFile = new File(file.getFilePath());

            if (diskFile.exists()) {
                if (diskFile.delete()) {
                    System.out.println("🗑 Deleted expired file: " + file.getFileName());
                } else {
                    System.out.println("❌ Failed to delete: " + file.getFileName());
                    continue;
                }
            }

            fileRepository.delete(file); // ✅ Remove metadata
        }
    }
}
