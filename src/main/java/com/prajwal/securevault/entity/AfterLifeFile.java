package com.prajwal.securevault.entity;

import jakarta.persistence.*;

@Entity
public class AfterLifeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long fileId;        // ✅ FileEntity ID – needed for download link
    private String fileName;
    private String filePath;

    private boolean delivered = false;

    // ✅ Constructors
    public AfterLifeFile() {}

    public AfterLifeFile(Long userId, Long fileId, String fileName, String filePath) {
        this.userId = userId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.delivered = false;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
