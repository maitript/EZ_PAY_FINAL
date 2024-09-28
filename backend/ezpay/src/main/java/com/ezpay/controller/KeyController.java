package com.ezpay.controller;

import org.springframework.stereotype.Controller;

import com.ezpay.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class KeyController {

    @Autowired
    private KeyService keyService;

    // Method to register a key for a user
    public void registerKey(Long customerId) {
        try {
            keyService.registerKey(customerId);
            System.out.println("Registering key for user: " + customerId);
            System.out.println("Key registered successfully for user: " + customerId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to update the key for a user (e.g., during password reset)
    public void checkUpdateKey(Long customerId) {
        try {
            keyService.updateKey(customerId);
            System.out.println("Updating key for user: " + customerId);
            System.out.println("Key updated successfully for user: " + customerId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    // Method to encrypt a given plaintext for a specific user
    public String encryptText(String plaintext, Long customerId) throws Exception {
        try {
            System.out.println("Encrypting text for user: " + customerId);
            String encryptedText = keyService.encryptText(plaintext, customerId);
            if (encryptedText != null) {
                System.out.println("Encrypted text for user " + customerId + ": " + encryptedText);
            } else {
                System.out.println("Encryption failed for user: " + customerId);
            }
            return encryptedText;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
            //return null; // Failed encryption, in actual implementation, throw the error instead of catching it.
        }
    }

    // Method to decrypt a given ciphertext for a specific user
    public String decryptText(String encryptedText, Long customerId) throws Exception {
        try {
        	
            System.out.println("Decrypting text for user: " + customerId);
            String decryptedText = keyService.decryptText(encryptedText, customerId);
            if (decryptedText != null) {
                System.out.println("Decrypted text for user " + customerId + ": " + decryptedText);
            } else {
                System.out.println("Decryption failed for user: " + customerId);
            }
            return decryptedText;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
            //return null; // Failed decryption, in actual implementation, throw the error instead of catching it.
        }
    }
}
