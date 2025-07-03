package com.prajwal.securevault.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.SecureRandom;
;


@Service
public class EncryptionService {

    private static final String AES = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 16; // 128 bits for demo, can do 32 for 256 bits with config
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_SIZE = 12; // GCM standard

    // Encrypt file and save encrypted bytes with IV prepended
    public void encryptFile(MultipartFile file, String secretKey, String destinationPath) throws Exception {
        byte[] key = secretKey.getBytes("UTF-8");
        SecretKeySpec keySpec = new SecretKeySpec(key, AES);

        // Generate IV
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] fileBytes = file.getBytes();
        byte[] encryptedBytes = cipher.doFinal(fileBytes);

        // Write IV + encrypted data to file
        try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
            fos.write(iv);
            fos.write(encryptedBytes);
        }
    }


}
