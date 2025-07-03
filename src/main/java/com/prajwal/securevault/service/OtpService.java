package com.prajwal.securevault.service;

import com.prajwal.securevault.entity.FileEntity;
import com.prajwal.securevault.entity.Otp;
import com.prajwal.securevault.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.security.SecureRandom; // 🔐 At the top of your file


@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom(); // ✅ Add this once


    // ✅ 1. Generate OTP for Forgot Password

    public String generateOtp(String email) {
        String otpCode = generateRandomOtp();
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setResendCount(0);
        otpRepository.save(otp);
        return otpCode;
    }

    // ✅ 2. Generate OTP for File Sharing with limit check
    public String generateOtpForFile(FileEntity file, String recipientEmail) {
        String otpCode = generateRandomOtp();

        Optional<Otp> existingOtpOpt = otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(file.getId(), recipientEmail);

        if (existingOtpOpt.isPresent()) {
            Otp existingOtp = existingOtpOpt.get();

            if (existingOtp.getResendCount() >= 3) {
                System.out.println("❌ Resend limit reached for " + recipientEmail);
                return null;
            }

            existingOtp.setOtpCode(otpCode);
            existingOtp.setCreatedAt(LocalDateTime.now());
            existingOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
            existingOtp.setResendCount(existingOtp.getResendCount() + 1);
            otpRepository.save(existingOtp);
            return otpCode;
        }

        // First-time send
        Otp otp = new Otp();
        otp.setOtpCode(otpCode);
        otp.setFile(file);
        otp.setEmail(recipientEmail);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otp.setResendCount(1); // First send
        otpRepository.save(otp);
        return otpCode;
    }

    // ✅ 3. Validate forgot-password OTP
    public boolean validateOtp(String email, String otpCode) {
        Optional<Otp> otpOpt = otpRepository.findByEmailAndOtpCode(email, otpCode);
        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            if (otp.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otp); // One-time use
                return true;
            }
        }
        return false;
    }

    // ✅ 4. Validate File Download OTP
    // ✅ 4. Validate File Download OTP
    public boolean verifyFileOtp(Long fileId, String otpCode) {
        Optional<Otp> optionalOtp = otpRepository.findByFile_IdAndOtpCode(fileId, otpCode);

        if (optionalOtp.isPresent()) {
            Otp otp = optionalOtp.get();

            if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                System.out.println("⏰ OTP expired.");
                return false;
            }

            // ✅ Check max usage
            if (otp.getResendCount() >= 3) {
                System.out.println("⛔ OTP usage limit exceeded (max 3).");
                return false;
            }

            // ✅ Increment resendCount before deleting
            otp.setResendCount(otp.getResendCount() + 1);
            otpRepository.save(otp);

//            otpRepository.delete(otp); // one-time use OR comment this if keeping until 3 uses
            return true;
        }

        return false;
    }

    // ✅ 5. Send OTP manually if needed
    public void sendOtp(String email, String otpCode) {
        emailService.sendOtpEmail(email, otpCode);
    }

    // ✅ 6. Save manually if needed
    public void saveOtp(Otp otp) {
        otpRepository.save(otp);
    }

    // ✅ Random 6-digit generator
    private String generateRandomOtp() {
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }


    public Optional<Otp> getLatestOtp(Long fileId, String email) {
        return otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(fileId, email);
    }


    public String generateOtpForPhone(String phoneNumber) {
        String otpCode = generateRandomOtp();
        Otp otp = new Otp();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setResendCount(0);
        otpRepository.save(otp);
        return otpCode;
    }


    // ✅ 8. Validate OTP for Phone
    public boolean validateOtpByPhone(String phoneNumber, String otpCode) {
        Optional<Otp> otpOpt = otpRepository.findByPhoneNumberAndOtpCode(phoneNumber, otpCode);
        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            if (otp.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpRepository.delete(otp); // One-time use
                return true;
            }
        }
        return false;
    }



}
