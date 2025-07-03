//package com.prajwal.securevault.scheduler;
//
//import com.prajwal.securevault.entity.AfterLifeFile;
//import com.prajwal.securevault.entity.User;
//import com.prajwal.securevault.repository.AfterLifeFileRepository;
//import com.prajwal.securevault.repository.UserRepository;
//import com.prajwal.securevault.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import com.prajwal.securevault.service.EmailService;
//
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class AfterLifeScheduler {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private AfterLifeFileRepository afterLifeFileRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    // ⏱ Runs every 24 hours
//    @Scheduled(cron = "0 0 3 * * *") // runs daily at 3:00 AM
//    public void checkAndDeliverFiles() {
//        System.out.println("🌀 Scheduler triggered: Checking inactive users...");
//
//        List<User> users = userRepository.findAll();
//        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
//
//        for (User user : users) {
//            if (user.getLastLogin() == null || user.getTrustedEmail() == null) continue;
//            if (user.getLastLogin().isBefore(oneYearAgo)) {
//                List<AfterLifeFile> files = afterLifeFileRepository.findByUserIdAndDeliveredFalse(user.getId());
//
//                if (!files.isEmpty()) {
//                    StringBuilder emailBody = new StringBuilder();
//                    emailBody.append("Dear Trusted Contact,\n\n");
//                    emailBody.append("The user ").append(user.getUsername())
//                            .append(" has been inactive for over a year.\n\n");
//                    emailBody.append("Here are the files they've selected for you:\n\n");
//
//                    for (AfterLifeFile file : files) {
//                        String downloadLink = "http://localhost:8080/download-file/" + file.getFileId();
//                        emailBody.append("📁 ").append(file.getFileName())
//                                .append(" → ").append(downloadLink).append("\n");
//
//                        file.setDelivered(true); // mark as delivered
//                        afterLifeFileRepository.save(file);
//                    }
//
//                    emailBody.append("\nRegards,\nSecureVault After-Life System");
//
//                    // ✅ Send email
//                    emailService.sendCustomEmail(user.getTrustedEmail(),
//                            "💌 Files from " + user.getUsername(),
//                            emailBody.toString());
//
//                    System.out.println("✅ Files delivered to trusted email of " + user.getUsername());
//                }
//            }
//        }
//    }
//}
