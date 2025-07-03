package com.prajwal.securevault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Used for forgot password
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP code is: " + otpCode + "\nIt is valid for 5 minutes.");
        mailSender.send(message);
    }

    // Alias
    public void sendOtp(String toEmail, String otpCode) {
        sendOtpEmail(toEmail, otpCode);
    }

    // ✅ OLD: Send OTP and File name (still here)
    public void sendSharingOtp(String toEmail, String otpCode, String fileName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🔐 SecureVault File Sharing OTP");
        message.setText("Here is your OTP to access the shared file:\n\n"
                + "📁 File Name: " + fileName + "\n"
                + "🔐 OTP: " + otpCode + "\n"
                + "🕒 Valid for 10 minutes\n\n"
                + "Regards,\nSecureVault Team");
        mailSender.send(message);
    }

    // ✅ NEW: Send OTP + File Name + Direct Access Link
    public void sendSharingOtp(String toEmail, String otpCode, String fileName, Long fileId) {
        String link = "http://localhost:8080/access-file/" + fileId;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🔐 SecureVault File Sharing OTP + Access Link");
        message.setText("Hello,\n\n"
                + "You've received access to a file on SecureVault:\n\n"
                + "📁 File Name: " + fileName + "\n"
                + "🔐 OTP: " + otpCode + "\n"
                + "🕒 Valid for 10 minutes\n\n"
                + "👉 Click here to verify & download: " + link + "\n\n"
                + "Regards,\nSecureVault Team");
        mailSender.send(message);
    }
    // ✅ After-Life Email to Trusted Person
    public void sendAfterLifeFilesEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // ✅ Send a custom subject + message (used for after-life email)
    public void sendCustomEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
