package com.ezpay.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezpay.service.LoginService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Add this line to allow requests from React
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	// calls login service to check if userid already in use.
	@PutMapping("/check_user_id")
    public ResponseEntity<Map<String, String>> checkUserId(@RequestBody JsonNode payload) {
        //logger.info("Received request to check user ID: {}", payload);

        // Validate that the userId is present
        if (!payload.has("userId")) {
            return ResponseEntity.badRequest().body(Map.of("message", "User ID is required"));
        }

        String userId = payload.get("userId").asText();
        
        if (loginService.checkUserId(userId)) {
            return ResponseEntity.status(400).body(Map.of("message", "User ID already exists"));
        } else {
            return ResponseEntity.ok(Map.of("message", "User ID not present"));
        }
    }
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> loginUser(@RequestBody JsonNode payload) {
		//System.out.println("--------------->"+"Got here 2");

		String userId = payload.get("userId").asText();
	    String password = payload.get("password").asText();
		int isLoggedIn = loginService.authenticate(userId, password);
        
        if (isLoggedIn==1) {
        	boolean isProfileInfoSet = loginService.getIsProfileInfoSetStatus(userId);
        	Long customerId=loginService.getCustomerId(userId);
        	Map<String, Object> response = new HashMap<>();
        	
            response.put("message", "Login successful");
            response.put("profileInfoSetStatus", isProfileInfoSet);
            response.put("customerId", customerId);
            
        	return ResponseEntity.ok(response);
        } else if(isLoggedIn==0){
            return ResponseEntity.status(401).body(Map.of("message", "Invalid user ID or password"));
        }
        else {
        	return ResponseEntity.status(401).body(Map.of("message", "User Blocked due to 3 incorrect login attempts. Reset password to continue"));
        }
    }
	
	@PostMapping("/register-user")
    public ResponseEntity<String> registerUser(@RequestBody JsonNode payload) {
        //Customer customer = new Customer();
        // Populate customer fields from the request

        boolean isRegistered = loginService.registerUser(payload.get("userId").asText(), payload.get("password").asText());
        if (isRegistered) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.status(400).body("User ID already exists");
        }
    }
	
}
