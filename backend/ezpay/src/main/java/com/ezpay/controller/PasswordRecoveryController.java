package com.ezpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ezpay.service.PasswordRecoveryService;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Controller to handle password recovery requests, such as forgot password and resetting the password.
 */
@RestController
@RequestMapping("/api/password")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;
      
    /**
     * Endpoint to request a password reset token by providing an email or mobile number.
     *
     * @param emailOrMobile the user's registered email or mobile number to request a password reset
     * @return a ResponseEntity containing the status of the password reset request
     */
    
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String emailOrMobile) {
        try {
        	// Business Logic: Create a password reset token for the provided email or mobile
            String response = passwordRecoveryService.createPasswordResetToken(emailOrMobile);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
        	 // Business Logic: If an invalid email or mobile is provided, return an error message
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to reset the password using the provided token and new password.
     *
     * @param token the token used to authenticate the password reset request
     * @param newPassword the new password to be set for the user
     * @return a ResponseEntity indicating whether the password reset was successful
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
        	 // Business Logic: Resets the password if the token is valid
            String response = passwordRecoveryService.resetPassword(token, newPassword);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
        	 // Business Logic: If the token is invalid or expired, return an error message
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
