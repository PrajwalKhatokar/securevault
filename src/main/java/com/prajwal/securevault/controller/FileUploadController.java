
package com.prajwal.securevault.controller;

import com.prajwal.securevault.entity.FileEntity;
import com.prajwal.securevault.repository.FileRepository;
import com.prajwal.securevault.service.EmailService;
import com.prajwal.securevault.service.FileService;
import com.prajwal.securevault.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.prajwal.securevault.service.EncryptionService;
import com.prajwal.securevault.service.DecryptionService;
import org.springframework.core.io.ByteArrayResource;
import com.prajwal.securevault.util.ByteArrayMultipartFile;
import com.prajwal.securevault.repository.DownloadLogRepository;
import com.prajwal.securevault.entity.DownloadLog;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.security.core.context.SecurityContextHolder;






import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
public class FileUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private DecryptionService decryptionService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private DownloadLogRepository downloadLogRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/upload")
    public String showUploadForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<FileEntity> files = fileRepository.findByUploadedBy(username);
        model.addAttribute("files", files);
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("expiry") String expiryOption,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "❌ Please select a file to upload.");
            return "redirect:/upload";
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // ✅ Save as plain file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Plain file saved at: " + filePath);

            // ⏱️ Expiry metadata
            LocalDateTime uploadedAt = LocalDateTime.now();
            LocalDateTime expiresAt = fileService.calculateExpiry(uploadedAt, expiryOption);

            FileEntity metadata = new FileEntity(
                    fileName,
                    filePath.toString(),
                    authentication.getName(),
                    uploadedAt,
                    file.getSize(),
                    expiresAt
            );
            metadata.setEncrypted(false); // ✅ Plain on upload
            fileRepository.save(metadata);

            redirectAttributes.addFlashAttribute("message", "✅ File uploaded successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "❌ Upload failed: " + e.getMessage());
        }

        return "redirect:/upload";
    }



    @GetMapping("/download-by-id/{id}")
    public ResponseEntity<Resource> downloadById(@PathVariable Long id, HttpServletRequest request) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileEntity file = optionalFile.get();

        try {
            byte[] fileBytes;
            if (file.isEncrypted()) {
                fileBytes = decryptionService.decryptFile(file.getFilePath(), "MySecretAESKey12");
            } else {
                fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
            }

            // ✅ Log for logged-in user
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            DownloadLog log = new DownloadLog();
            log.setFile(file);
            log.setIpAddress(request.getRemoteAddr());
            log.setDownloadedAt(LocalDateTime.now());
            log.setDownloadedBy(username);
            downloadLogRepository.save(log);

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);

        if (optionalFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "❌ File not found.");
            return "redirect:/upload";
        }

        FileEntity file = optionalFile.get();

        if (!file.getUploadedBy().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("message", "❌ You are not authorized to delete this file.");
            return "redirect:/upload";
        }

        try {
            Path path = Paths.get(file.getFilePath());
            Files.deleteIfExists(path);
            fileRepository.delete(file);
            redirectAttributes.addFlashAttribute("message", "✅ File deleted successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "❌ Failed to delete file: " + e.getMessage());
        }

        return "redirect:/upload";
    }

    // ✅ GET /share/{id} - View OTP and file
    @GetMapping("/share/{id}")
    public String generateOtpForSharedFile(@PathVariable Long id, Model model) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);

        if (optionalFile.isEmpty()) {
            return "redirect:/upload";
        }

        FileEntity file = optionalFile.get();

        // ✅ FIXED: Pass both file and uploader email
        String otp = otpService.generateOtpForFile(file, file.getUploadedBy());

        model.addAttribute("otp", otp);
        model.addAttribute("fileName", file.getFileName());
        model.addAttribute("fileId", file.getId());

        return "share-otp";
    }


//    @GetMapping("/access-file/{id}")
//    public String accessFile(@PathVariable Long id, Model model) {
//        model.addAttribute("fileId", id); // pass fileId to the view
//        return "verify-otp"; // ✅ must match the HTML filename in templates folder
//    }


    // ✅ POST /share - Share via email
    @PostMapping("/share")
    public String shareFileWithOtp(@RequestParam("fileName") String fileName,
                                   @RequestParam("recipientEmail") String recipientEmail,
                                   @RequestParam("fileId") Long fileId,
                                   RedirectAttributes redirectAttributes) {

        Optional<FileEntity> optionalFile = fileRepository.findById(fileId);
        if (optionalFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ File not found!");
            return "redirect:/upload";
        }

        FileEntity file = optionalFile.get();

        // ✅ BLOCK sharing for files that are set to expire
        if (file.getExpiresAt() != null) {
            redirectAttributes.addFlashAttribute("error",
                    "⛔ Cannot share file with expiration. Please upload using 'Never Delete' option.");
            return "redirect:/upload";
        }

        try {
            // 🔐 Encrypt only if not already encrypted
            if (!file.isEncrypted()) {
                String secretKey = "MySecretAESKey12";

                Path originalPath = Paths.get(file.getFilePath());
                byte[] fileBytes = Files.readAllBytes(originalPath);

                MultipartFile toEncryptFile = new ByteArrayMultipartFile(
                        fileBytes,
                        file.getFileName(),
                        "application/octet-stream"
                );

                encryptionService.encryptFile(toEncryptFile, secretKey, file.getFilePath());

                file.setEncrypted(true);
                fileRepository.save(file);
            }

            // ✅ Save recipient email
            file.setSharedWith(recipientEmail);
            fileRepository.save(file);

            // ✅ Generate OTP & send
            String otp = otpService.generateOtpForFile(file, recipientEmail);
            emailService.sendSharingOtp(recipientEmail, otp, fileName, fileId);

            redirectAttributes.addFlashAttribute("message", "✅ OTP sent successfully to recipient!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Failed to share: " + e.getMessage());
        }

        return "redirect:/otp-sent";
    }



    // ✅ FINAL STEP: OTP Verification and Redirect to Download Page
    @PostMapping("/verify-share-otp")
    public String verifySharedOtp(@RequestParam("fileId") Long fileId,
                                  @RequestParam("otp") String otpCode,
                                  Model model) {

        boolean isValid = otpService.verifyFileOtp(fileId, otpCode);

        if (isValid) {
            model.addAttribute("fileId", fileId);
            return "download-file";  // ✅ OTP correct, show download
        } else {
            model.addAttribute("error", "❌ Invalid or expired OTP. Please try again.");
            model.addAttribute("fileId", fileId);  // retry again with same fileId
            return "verify-otp";  // ✅ Back to OTP input form
        }
    }
    @GetMapping("/access-history")
    public String viewAccessHistory(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<FileEntity> files = fileRepository.findByUploadedBy(username);

        Map<String, List<DownloadLog>> accessMap = new LinkedHashMap<>();
        for (FileEntity file : files) {
            List<DownloadLog> logs = downloadLogRepository.findByFile(file);
            accessMap.put(file.getFileName(), logs);
        }

        model.addAttribute("accessMap", accessMap);
        return "access-history";
    }

    @GetMapping("/afterlife-download/{id}")
    public ResponseEntity<Resource> downloadAfterLifeFile(@PathVariable Long id) {
        Optional<FileEntity> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileEntity file = optionalFile.get();

        try {
            // 🔓 NO decryption - send raw bytes
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace(); // log error
            return ResponseEntity.internalServerError().build();
        }
    }

}
