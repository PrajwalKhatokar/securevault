package com.prajwal.securevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling // ✅ Enables scheduled tasks like cleanup jobs
public class SecureDigitalVaultApplication {

    // ✅ Optional: Set default timezone for consistency

    public static void main(String[] args) {
        SpringApplication.run(SecureDigitalVaultApplication.class, args);
        System.out.println("🚀 Secure Digital Vault Application Started Successfully!");
    }
}
