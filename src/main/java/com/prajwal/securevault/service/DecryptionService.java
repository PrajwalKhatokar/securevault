package com.prajwal.securevault.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.security.SecureRandom;

@Service
public class DecryptionService {

    private static final String AES = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_SIZE = 12;

    // 🔓 Decrypt and return plain file bytes
    public byte[] decryptFile(String filePath, String secretKey) throws Exception {
        byte[] key = secretKey.getBytes("UTF-8");
        SecretKeySpec keySpec = new SecretKeySpec(key, AES);

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] iv = new byte[IV_SIZE];
            fis.read(iv); // read IV from start

            byte[] encryptedData = fis.readAllBytes(); // rest is encrypted content

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            return cipher.doFinal(encryptedData);
        }
    }
}
