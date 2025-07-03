package com.prajwal.securevault.repository;

import com.prajwal.securevault.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    // For Forgot Password
    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);

    void deleteByEmail(String email);

    // For File Sharing: Verify OTP
    Optional<Otp> findByFile_IdAndOtpCode(Long fileId, String otpCode);

    // Latest OTP by file and email (for resend logic)
    Optional<Otp> findTopByFile_IdAndEmailOrderByCreatedAtDesc(Long fileId, String email);

    Optional<Otp> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode);
    void deleteByPhoneNumber(String phoneNumber);


}
