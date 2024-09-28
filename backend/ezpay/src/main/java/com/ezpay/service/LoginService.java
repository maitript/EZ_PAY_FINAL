package com.ezpay.service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Sagar Kumar
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ezpay.controller.FraudDetectionController;
import com.ezpay.controller.KeyController;
import com.ezpay.entity.Customer;
import com.ezpay.entity.LoginData;
import com.ezpay.entity.SuspiciousActivity;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.repository.SuspiciousActivityRepository;
import com.ezpay.utils.PasswordUtils;

import jakarta.transaction.Transactional;

@Service
public class LoginService {
	
	 @Autowired
	 private LoginDataRepository loginDataRepository;
	 
//	 @Autowired
//	 private MasterDataRepository masterDataRepository;
	 
	 @Autowired
	 private RegistrationService masterDataservice;

	@Autowired
	 private KeyController keyController;
	 
	 @Autowired
	 private SuspiciousActivityRepository suspiciousActivityRepository;
	 
	 @Autowired
	 private FraudDetectionController fraudDetectionController;
	 
	@Transactional
	 public int authenticate(String userId, String password) {
	        // Find login data by user ID
	        Optional<LoginData> loginDataOpt = loginDataRepository.findById(userId);
	        
	        
	        if (loginDataOpt.isPresent()) {
	            LoginData loginData = loginDataOpt.get();
			 if(loginData.getSuspiciousActivity().getBlockId()==1) //Success full Login
		            	 return 2;
		            //Use case 5 functionality to checkUpdate
		            keyController.checkUpdateKey(loginData.getCustomer().getCustomerId());
	            
	            // Compare the hashed password with the provided password
	            boolean result= PasswordUtils.verifyPassword(password, loginData.getPasswordHash());
		    if(!result)
		    {
	            	fraudDetectionController.flagLoginAttempt(loginData.getCustomer().getCustomerId());	
		    }
		 return result ? 1 : 0;
	        }
	        return 0; // User not found
	 }
	 
	 public boolean checkUserId(String userId){
		return loginDataRepository.findById(userId).isPresent();
	 }
	 @Transactional
	 public boolean registerUser(String userId, String password)
	 {
		// Check if userId already exists
	        if (loginDataRepository.findByUserId(userId).isPresent()) {
	            return false; // userId already exists
	        }
	        // Creating a temporary customer object for initial registration.
	        Customer customer = masterDataservice.AddTempProfileDetailsAndSave(userId);
	        
	        String hashedPassword=PasswordUtils.hashPassword(password);
	        
	        // Create LoginData object
	        LoginData loginData = new LoginData();
	        loginData.setUserId(userId);
	        loginData.setPasswordHash(hashedPassword);
	        
	        loginData.setCustomer(customer);
	        
	        //loginData.setBlockedCode(0); // Default value
	         
		 //Change made by UC5 , before blockId 0 was entered, now the corresponding entity is added
	        SuspiciousActivity sus = suspiciousActivityRepository.findById(0).orElse(null);
	        loginData.setSuspiciousActivity(sus); // Default value
	        
	        // Save the LoginData
	        loginDataRepository.save(loginData);
	        return true; // Registration successful
	 }

	public boolean getIsProfileInfoSetStatus(String userId) {
	    Boolean profileSetStatus = loginDataRepository.findById(userId).get().getCustomer().getIsProfileInfoSet();
	    
		return profileSetStatus;
	}

	public Long getCustomerId(String userId) {
		
		return loginDataRepository.findById(userId).get().getCustomer().getCustomerId();
	}
	
}
