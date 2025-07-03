package com.prajwal.securevault.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FileService {

    public LocalDateTime calculateExpiry(LocalDateTime uploadedAt, String expiryOption) {
        switch (expiryOption) {
            case "1_MIN":
                return uploadedAt.plusMinutes(1);
            case "5_MIN":
                return uploadedAt.plusMinutes(5);
            case "3_DAYS":
                return uploadedAt.plusDays(3);
            case "7_DAYS":
                return uploadedAt.plusDays(7);
            case "30_DAYS":
                return uploadedAt.plusDays(30);
            case "NEVER":
                return null;
            default:
                // Try parsing numeric minutes from expiryOption
                try {
                    long minutes = Long.parseLong(expiryOption);
                    if (minutes == 0) {
                        return null; // 0 means never expire
                    }
                    return uploadedAt.plusMinutes(minutes);
                } catch (NumberFormatException e) {
                    // If parsing fails, fallback to no expiry
                    return null;
                }
        }
    }
}
