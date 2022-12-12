package dev.test.take_a_walk_duo.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {
    // hashSha512 로직
    public static String hashSha512(String input) {
        try {
            StringBuilder passwordHashBuilder = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.reset();
            md.update(input.getBytes(StandardCharsets.UTF_8));
            for (byte hashByte : md.digest()) {
                passwordHashBuilder.append(String.format("%02x", hashByte));
            }
            return passwordHashBuilder.toString();
        } catch (NoSuchAlgorithmException ignored) {
            // NoSuchAlgorithmException 발생시 null 반환
            return null;
        }
    }
    // CryptoUtils 클래스 객체화 못하게 막기 위해 private 사용
    private CryptoUtils() {

    }
}