package com.prajwal.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Email of the recipient (for both password reset and file sharing)
    private String email;

    // ✅ Generated 6-digit OTP code
    private String otpCode;

    // ✅ When the OTP will expire
    private LocalDateTime expiryTime;

    // ✅ When the OTP was created
    private LocalDateTime createdAt;

    // ✅ Tracks how many times OTP was resent (default 0)
    private int resendCount = 0;

    // ✅ For file-sharing scenario (file associated with this OTP)

    // ✅ Add this field after resendCount
    private boolean downloaded = false;

    // ✅ Getter and Setter
    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
