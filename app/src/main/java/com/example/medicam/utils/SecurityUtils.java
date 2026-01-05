package com.example.medicam.utils;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for security operations like password hashing
 */
public class SecurityUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hashes a password with a random salt using SHA-256
     * @param password The plain text password
     * @return A string in format "salt:hash" for storage
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Encode both salt and hash
            String saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP);
            String hashBase64 = Base64.encodeToString(hashedPassword, Base64.NO_WRAP);
            
            // Return combined string
            return saltBase64 + ":" + hashBase64;
            
        } catch (NoSuchAlgorithmException e) {
            // Fallback: just return a simple hash (not recommended for production)
            return simpleHash(password);
        }
    }
    
    /**
     * Verifies a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash in format "salt:hash"
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            // Check if it's a salted hash
            if (storedHash.contains(":")) {
                String[] parts = storedHash.split(":");
                if (parts.length != 2) {
                    return false;
                }
                
                // Decode salt
                byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
                String storedHashPart = parts[1];
                
                // Hash the input password with the same salt
                MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
                md.update(salt);
                byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
                String hashBase64 = Base64.encodeToString(hashedPassword, Base64.NO_WRAP);
                
                return hashBase64.equals(storedHashPart);
            } else {
                // Legacy: plain text comparison (for old accounts)
                // This allows existing users to still login
                return password.equals(storedHash);
            }
            
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            // If decoding fails, try plain text comparison (legacy support)
            return password.equals(storedHash);
        }
    }
    
    /**
     * Hashes a PIN (simpler hash without salt for PIN)
     * @param pin The 4-digit PIN
     * @return Hashed PIN
     */
    public static String hashPin(String pin) {
        return simpleHash(pin);
    }
    
    /**
     * Verifies a PIN against stored hash
     * @param pin The entered PIN
     * @param storedHash The stored PIN hash
     * @return true if PIN matches
     */
    public static boolean verifyPin(String pin, String storedHash) {
        if (pin == null || storedHash == null) {
            return false;
        }
        
        // Check if it's hashed or plain (legacy support)
        String hashedPin = simpleHash(pin);
        if (hashedPin.equals(storedHash)) {
            return true;
        }
        
        // Legacy: plain text comparison
        return pin.equals(storedHash);
    }
    
    /**
     * Simple SHA-256 hash without salt
     */
    private static String simpleHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            // Should never happen as SHA-256 is always available
            return input;
        }
    }
    
    /**
     * Validates password strength
     * @param password The password to validate
     * @return Error message or null if valid
     */
    public static String validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (password.length() > 50) {
            return "Password is too long";
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        if (!hasLetter || !hasDigit) {
            return "Password must contain both letters and numbers";
        }
        
        return null; // Valid
    }
    
    /**
     * Validates phone number format
     * @param phone The phone number
     * @return Error message or null if valid
     */
    public static String validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number is required";
        }
        
        // Remove any spaces or dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        
        if (!cleanPhone.matches("\\d+")) {
            return "Phone number should contain only digits";
        }
        
        if (cleanPhone.length() != 10) {
            return "Please enter a valid 10-digit phone number";
        }
        
        return null; // Valid
    }
    
    /**
     * Validates name
     * @param name The name to validate
     * @return Error message or null if valid
     */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        
        if (name.trim().length() < 2) {
            return "Name is too short";
        }
        
        if (name.length() > 100) {
            return "Name is too long";
        }
        
        // Check for valid characters (letters, spaces, and common name characters)
        if (!name.matches("^[a-zA-Z\\s.'-]+$")) {
            return "Name contains invalid characters";
        }
        
        return null; // Valid
    }
}
