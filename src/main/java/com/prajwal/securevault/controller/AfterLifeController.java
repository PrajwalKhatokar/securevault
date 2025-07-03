package com.prajwal.securevault.controller;

import com.prajwal.securevault.entity.AfterLifeFile;
import com.prajwal.securevault.entity.FileEntity;
import com.prajwal.securevault.entity.User;
import com.prajwal.securevault.repository.AfterLifeFileRepository;
import com.prajwal.securevault.repository.FileEntityRepository;
import com.prajwal.securevault.repository.UserRepository;
import com.prajwal.securevault.service.DecryptionService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/afterlife")
public class AfterLifeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private AfterLifeFileRepository afterLifeFileRepository;

    @Autowired
    private DecryptionService decryptionService;

    // ✅ Step 1: Select files
    @GetMapping("/select-files")
    public String showFileSelectionForm(Model model, Principal principal) {
        String username = principal.getName();
        List<FileEntity> userFiles = fileEntityRepository.findByUploadedBy(username);
        model.addAttribute("files", userFiles);
        return "afterlife-select-files";
    }

    // ✅ Step 2: Save selection
    @PostMapping("/select-files")
    public String saveAfterLifeFiles(@RequestParam("fileIds") List<Long> fileIds, Principal principal) {
        String username = principal.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/afterlife/select-files?error=user-not-found";
        }
        Long userId = userOpt.get().getId();

        for (Long fileId : fileIds) {
            Optional<FileEntity> fileOpt = fileEntityRepository.findById(fileId);
            if (fileOpt.isPresent()) {
                FileEntity fileEntity = fileOpt.get();

                AfterLifeFile afterLifeFile = new AfterLifeFile();
                afterLifeFile.setUserId(userId);
                afterLifeFile.setFileId(fileEntity.getId());
                afterLifeFile.setFileName(fileEntity.getFileName());
                afterLifeFile.setFilePath(fileEntity.getFilePath());
                afterLifeFile.setDelivered(false);

                afterLifeFileRepository.save(afterLifeFile);
            }
        }

        return "redirect:/afterlife/select-files?success";
    }

    // ✅ Step 3: Trusted email
    @GetMapping("/trusted-email")
    public String showTrustedEmailForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("trustedEmail", user.getTrustedEmail());
        return "trusted-email";
    }

    @PostMapping("/trusted-email")
    public String saveTrustedEmail(@RequestParam String trustedEmail, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTrustedEmail(trustedEmail);
        userRepository.save(user);
        return "redirect:/afterlife/trusted-email?success";
    }

    // ✅ Step 4: Download After-Life File
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadAfterLifeFile(@PathVariable Long id, HttpServletRequest request) {
        Optional<FileEntity> optionalFile = fileEntityRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileEntity file = optionalFile.get();

        try {
            byte[] fileBytes;
            if (file.isEncrypted()) {
                // Must match the encryption key
                fileBytes = decryptionService.decryptFile(file.getFilePath(), "MySecretAESKey12");
            } else {
                fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
            }

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return ResponseEntity.internalServerError()
                    .body(new ByteArrayResource(("❌ Decryption failed: " + e.getMessage()).getBytes()));
        }
    }

    // ✅ Step 5: View Trusted Files
    @GetMapping("/trusted-files")
    public String viewTrustedFiles(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AfterLifeFile> trustedFiles = afterLifeFileRepository.findByUserIdAndDeliveredFalse(user.getId());
        model.addAttribute("trustedFiles", trustedFiles);
        return "view-trusted-files";
    }

}
