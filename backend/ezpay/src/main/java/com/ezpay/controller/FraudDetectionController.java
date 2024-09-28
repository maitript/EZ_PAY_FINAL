package com.ezpay.controller;

import com.ezpay.service.FraudDetectionService;


//import com.ezpay.service.TokenService;
//import com.ezpay.service.TransactionService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController // Only for REST endpoints
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from React app
public class FraudDetectionController {

    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    //@Autowired
    //private TransactionService transactionService;
    
    //@Autowired
    //private TokenService tokenService;

    // Standard internal method, not a REST endpoint
    public boolean flagLoginAttempt(Long customerId) {
        boolean isBlocked = fraudDetectionService.flagLoginAttempt(customerId);
        if (isBlocked) {
            System.out.println("User with customerId " + customerId + " is blocked due to suspicious login attempts.");
        } else {
            System.out.println("Login attempt for customerId " + customerId + " is not suspicious.");
        }
        return isBlocked;
    }

    // Standard internal method, not a REST endpoint
   /* public boolean flagTransactionForToken(Integer customerId) {
        return fraudDetectionService.flagTransaction(customerId);
    }

    // Expose this method as a REST endpoint
    @PostMapping("/flagTransaction")
    public boolean flagTransaction(@RequestParam Integer customerId) {
        boolean isBlocked = fraudDetectionService.flagTransaction(customerId);
        if (isBlocked) {
            System.out.println("Transaction for customerId " + customerId + " is blocked due to high amount.");
        } else {
            System.out.println("Transaction for customerId " + customerId + " is not flagged.");
        }
        return isBlocked;
    }
    
    
    @PostMapping("/generate-token")
    public String generateToken(@RequestBody HashMap<String, String> request) {
        String customerId = request.get("customerId");
        return tokenService.generateToken(customerId);
    }

    @PostMapping("/validate-token")
    public boolean validateToken(@RequestBody HashMap<String, String> request) {
        String customerId = request.get("customerId");
        String token = request.get("token");
        boolean isValid = tokenService.validateToken(customerId, token);
        
        if (isValid) {
            // Update transaction status to success if the token is valid
            transactionService.updateTransactionStatus(Integer.parseInt(customerId));
        }
        
        return isValid;

    }*/
}
