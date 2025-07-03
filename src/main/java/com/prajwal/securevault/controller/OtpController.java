// OtpController.java
package com.prajwal.securevault.controller;

import com.prajwal.securevault.entity.DownloadLog;
import com.prajwal.securevault.entity.FileEntity;
import com.prajwal.securevault.entity.Otp;
import com.prajwal.securevault.repository.DownloadLogRepository;
import com.prajwal.securevault.repository.FileRepository;
import com.prajwal.securevault.repository.OtpRepository;
import com.prajwal.securevault.service.DecryptionService;
import com.prajwal.securevault.service.EmailService;
import com.prajwal.securevault.service.EncryptionService;
import com.prajwal.securevault.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class OtpController {

    @Autowired private FileRepository fileRepository;
    @Autowired private OtpService otpService;
    @Autowired private EncryptionService encryptionService;
    @Autowired private DecryptionService decryptionService;
    @Autowired private DownloadLogRepository downloadLogRepository;
    @Autowired private EmailService emailService;
    @Autowired private OtpRepository otpRepository;

    // ✅ Step 1: Show OTP form
    @GetMapping("/access-file/{id}")
    public String showOtpForm(@PathVariable Long id, Model model) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);
        if (optionalFile.isPresent()) {
            FileEntity file = optionalFile.get();
            model.addAttribute("fileId", file.getId());
            model.addAttribute("fileName", file.getFileName());
            model.addAttribute("email", file.getSharedWith());

            Optional<Otp> latestOtp = otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(id, file.getSharedWith());
            model.addAttribute("alreadyDownloaded", latestOtp.isPresent() && latestOtp.get().isDownloaded());
        } else {
            model.addAttribute("error", "❌ File not found.");
            model.addAttribute("alreadyDownloaded", false);
        }
        return "verify-share-otp";
    }

    @PostMapping("/send-otp-email")
    public String sendOtpEmail(@RequestParam String email, @RequestParam String otp,
                               @RequestParam String fileName, @RequestParam Long fileId) {
        emailService.sendSharingOtp(email, otp, fileName, fileId);
        return "redirect:/otp-sent";
    }

    @GetMapping("/otp-sent")
    public String otpSentPage() {
        return "otp-sent";
    }

    @PostMapping("/verify-file-otp")
    public String verifyOtpAndDownload(
            @RequestParam Long fileId,
            @RequestParam(name = "otp") String otpCode,
            Model model) {

        Optional<FileEntity> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isPresent()) {
            FileEntity file = fileOpt.get();
            String email = file.getSharedWith();

            Optional<Otp> otpOpt = otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(fileId, email);
            if (otpOpt.isPresent()) {
                Otp otp = otpOpt.get();

                if (otp.isDownloaded()) {
                    model.addAttribute("fileId", fileId);
                    model.addAttribute("fileName", file.getFileName());
                    model.addAttribute("email", email);
                    model.addAttribute("error", "✅ This file has already been downloaded using your OTP.");
                    return "verify-share-otp";
                }

                boolean isValid = otpService.verifyFileOtp(fileId, otpCode);
                if (isValid) {
                    return "redirect:/shared-download/" + fileId; // ✅ NEW: redirect to Thymeleaf page
                }

            }
        }

        model.addAttribute("fileId", fileId);
        model.addAttribute("error", "❌ Invalid or expired OTP. Please try again.");
        return "verify-share-otp";
    }


    // ✅ DOWNLOAD FILE (with encryption check)
    @GetMapping("/download-file/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "❌ File not found");
            return;
        }

        FileEntity file = optionalFile.get();

        // ✅ Mark OTP as downloaded
        Optional<Otp> otpOpt = otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(file.getId(), file.getSharedWith());
        otpOpt.ifPresent(otp -> {
            if (!otp.isDownloaded()) {
                otp.setDownloaded(true);
                otpRepository.save(otp);
            }
        });

        // ✅ Log download
        DownloadLog log = new DownloadLog();
        log.setFile(file);
        log.setIpAddress(request.getRemoteAddr());
        log.setDownloadedAt(java.time.LocalDateTime.now());
        log.setDownloadedBy(file.getSharedWith());
        downloadLogRepository.save(log);

        try {
            byte[] fileBytes;

            // ✅ 🔐 Force decryption if file name ends with .enc (even if encrypted flag is wrong)
            boolean isActuallyEncrypted = file.getFilePath().endsWith(".enc");

            if (file.isEncrypted() || isActuallyEncrypted) {
                String secretKey = "MySecretAESKey12";
                fileBytes = decryptionService.decryptFile(file.getFilePath(), secretKey);
            } else {
                fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
            response.getOutputStream().write(fileBytes);
            response.flushBuffer();

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "❌ Download failed: " + e.getMessage());
        }
    }

    // ✅ Step 5: Resend OTP with max limit
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam Long fileId,
                            @RequestParam String fileName,
                            @RequestParam String recipientEmail,
                            RedirectAttributes redirectAttributes) {

        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ Email is required.");
            return "redirect:/access-file/" + fileId;
        }

        Optional<FileEntity> optionalFile = fileRepository.findById(fileId);
        if (optionalFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ File not found.");
            return "redirect:/upload";
        }

        FileEntity file = optionalFile.get();
        Optional<Otp> otpOpt = otpRepository.findTopByFile_IdAndEmailOrderByCreatedAtDesc(fileId, recipientEmail);

        if (otpOpt.isPresent()) {
            Otp otp = otpOpt.get();
            if (otp.getResendCount() >= 3) {
                redirectAttributes.addFlashAttribute("error", "❌ Max resend limit (3) reached. Ask the sender to re-share.");
                return "redirect:/access-file/" + fileId;
            }

            otp.setResendCount(otp.getResendCount() + 1);
            otp.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));
            otpRepository.save(otp);

            emailService.sendSharingOtp(recipientEmail, otp.getOtpCode(), fileName, fileId);
            redirectAttributes.addFlashAttribute("message", "✅ OTP resent successfully (" + otp.getResendCount() + "/3)");
        } else {
            String newOtp = otpService.generateOtpForFile(file, recipientEmail);
            if (newOtp == null) {
                redirectAttributes.addFlashAttribute("error", "❌ Unable to send OTP.");
                return "redirect:/access-file/" + fileId;
            }

            emailService.sendSharingOtp(recipientEmail, newOtp, fileName, fileId);
            redirectAttributes.addFlashAttribute("message", "✅ New OTP sent successfully!");
        }

        return "redirect:/access-file/" + fileId;
    }

    // ✅ NEW: Serve download-file.html with button (not direct download)
    @GetMapping("/shared-download/{id}")//clash wil stop betwen INACTIVE OVER A YEAR  FILE DOWNLOAD AND OTP DOWNLOAD REDIRECT TO DOWNLOAD PAGE THERE I CAN DOWNLOAD
    public String showSharedDownloadPage(@PathVariable Long id, Model model) {
        model.addAttribute("fileId", id);
        return "download-file"; // your Thymeleaf download page
    }

}

