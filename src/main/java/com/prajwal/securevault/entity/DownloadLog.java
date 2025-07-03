package com.prajwal.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DownloadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;

    private LocalDateTime downloadedAt;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getDownloadedAt() { return downloadedAt; }
    public void setDownloadedAt(LocalDateTime downloadedAt) { this.downloadedAt = downloadedAt; }

    public FileEntity getFile() { return file; }
    public void setFile(FileEntity file) { this.file = file; }

    @Column(name = "downloaded_by")
    private String downloadedBy;
    public String getDownloadedBy() {
        return downloadedBy;
    }

    public void setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

}
