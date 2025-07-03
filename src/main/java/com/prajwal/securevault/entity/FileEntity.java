package com.prajwal.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_entity")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private String uploadedBy;

    private LocalDateTime uploadedAt;

    private long fileSize;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // ✅ NEW FIELD: To store recipient email for shared file
    @Column(name = "shared_with")
    private String sharedWith;

    public FileEntity() {
    }
    @OneToMany(mappedBy = "file", cascade = CascadeType.REMOVE)
    private List<DownloadLog> logs;

    public FileEntity(String fileName, String filePath, String uploadedBy,
                      LocalDateTime uploadedAt, long fileSize, LocalDateTime expiresAt) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.fileSize = fileSize;
        this.expiresAt = expiresAt;
    }

    // ✅ Getter and Setter for sharedWith
    public String getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
    }

    // Existing getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", uploadedAt=" + uploadedAt +
                ", fileSize=" + fileSize +
                ", expiresAt=" + expiresAt +
                ", sharedWith='" + sharedWith + '\'' +
                '}';
    }

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @OneToMany(mappedBy = "file", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Otp> otps = new ArrayList<>();

    public List<Otp> getOtps() {
        return otps;
    }

    public void setOtps(List<Otp> otps) {
        this.otps = otps;
    }

    @Column(name = "encrypted")
    private boolean encrypted = false;

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

}
