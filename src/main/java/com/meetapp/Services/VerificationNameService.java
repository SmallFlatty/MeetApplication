package com.meetapp.Services;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class VerificationNameService {

    private static final String SECRET_KEY = "MASHONKA"; // має збігатися з фронтендом

    public static boolean verifySignedName(String signed) {
        try {
            String[] parts = signed.split("\\.");
            if (parts.length != 2) return false;

            String encodedName = parts[0];
            String givenSignature = parts[1];

            String expected = hmacSha256(encodedName, SECRET_KEY);
            return expected.equals(givenSignature);
        } catch (Exception e) {
            return false;
        }
    }

    public static String extractFullName(String signed) {
        String encodedName = signed.split("\\.")[0];
        return URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
    }

    private static String hmacSha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
