package com.ex.learninghub.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Slf4j
@Component
public class TotpService {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return encodeBase32(bytes);
    }

    public String getQrCodeUri(String secret, String email) {
        String issuer = "LearningHub-LMS";
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                encodedIssuer, encodedEmail, secret, encodedIssuer);
    }

    public boolean verifyTotpCode(String secretKey, String codeStr) {
        if (secretKey == null || codeStr == null || !codeStr.matches("^\\d{6}$")) {
            return false;
        }

        try {
            int inputCode = Integer.parseInt(codeStr);
            long timeWindow = System.currentTimeMillis() / 1000 / 30;

            // Check current, previous, and next 30-second window to accommodate clock drift
            for (int i = -1; i <= 1; i++) {
                int generatedCode = generateTotp(secretKey, timeWindow + i);
                if (generatedCode == inputCode) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Lỗi xác thực mã TOTP 2FA: {}", e.getMessage());
        }
        return false;
    }

    private int generateTotp(String secretKey, long timeStep) throws Exception {
        byte[] key = decodeBase32(secretKey);
        byte[] data = new byte[8];

        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (timeStep & 0xFF);
            timeStep >>= 8;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        int truncatedHash = ((hash[offset] & 0x7F) << 24) |
                ((hash[offset + 1] & 0xFF) << 16) |
                ((hash[offset + 2] & 0xFF) << 8) |
                (hash[offset + 3] & 0xFF);

        return truncatedHash % 1000000;
    }

    private String encodeBase32(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                sb.append(BASE32_CHARS.charAt(index));
            }
        }

        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            sb.append(BASE32_CHARS.charAt(index));
        }

        return sb.toString();
    }

    private byte[] decodeBase32(String secret) {
        secret = secret.toUpperCase().replaceAll("[^A-Z2-7]", "");
        byte[] bytes = new byte[secret.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (char c : secret.toCharArray()) {
            int val = BASE32_CHARS.indexOf(c);
            if (val < 0) continue;
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return bytes;
    }
}
