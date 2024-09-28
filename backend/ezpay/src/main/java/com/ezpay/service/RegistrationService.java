package com.ezpay.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Sagar Kumar
 */

import org.springframework.stereotype.Service;

import com.ezpay.controller.KeyController;
import com.ezpay.entity.Customer;
import com.ezpay.repository.MasterDataRepository;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

	@Autowired
	private MasterDataRepository masterDataRepository;
	
	@Autowired
	private KeyController keyController;
	
	@Autowired
	private KeyService keyService;
	
	@Transactional
	public Customer AddTempProfileDetailsAndSave(String userId) {
		Customer customer=new Customer();
		
		String userIdFiller= userId.length()>10 ? userId.substring(0, 9) : userId + "0".repeat(10-userId.length());
			
		
        customer.setName("TEMP_NAME"+userIdFiller); // Temporary value
        customer.setEmail("TEMP_EMAIL"+userIdFiller); // Temporary value
        customer.setMobileNumber(userIdFiller); // Temporary value
        customer.setAddress("TEMP_ADDRESS"+userIdFiller); // Temporary value
        customer.setDob(LocalDateTime.now()); // Temporary DOB
        customer.setGender("UNKOWN"); // Temporary value
        customer.setProfilePictureUrl(null); // Empty or placeholder for profile picture
        customer.setUpiId(userIdFiller); // Temporary UPI ID
        customer.setBankAccountNumber(userIdFiller); // Temporary bank account number
        customer.setIfscCode(userIdFiller); // Temporary IFSC code
        customer.setAccountType(1); 
        
        // Fixed values
        customer.setProfileCreationDate(LocalDateTime.now()); // Current date
        customer.setProfileLastUpdatedDate(LocalDateTime.now()); // Current date
        customer.setIsProfileInfoSet(false); // Initial setup
        
		masterDataRepository.save(customer);
		return customer;
	}
	
	@Transactional
	public boolean AddInitialProfileDetails(JsonNode payload) {
        Optional<Customer> customerOp = masterDataRepository.findById(payload.get("customerId").asLong());
        
        if(!customerOp.isPresent()) {
        return false; //User not found
        }

        Customer customer=customerOp.get();

        customer.setName(payload.get("name").asText());
        customer.setEmail(payload.get("email").asText());
        customer.setMobileNumber(payload.get("mobileNumber").asText());
        customer.setAddress(payload.get("address").asText());

        String dobString = payload.get("dob").asText("1800-01-01")+"T00:00:00";
        LocalDateTime dob = LocalDateTime.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        customer.setDob(dob);
        
        customer.setGender(payload.get("gender").asText());
        customer.setProfilePictureUrl(payload.get("profilePictureUrl").asText()); 
        customer.setIsProfileInfoSet(true); 
        
        String upiId=payload.get("mobileNumber").asText()+"@ezpay";
        customer.setUpiId(upiId);
     
		System.out.println("----------------------------> Got in to add initial data:--->"+payload.get("bankAccountNumber").asText());
		//String bankAccountNumber=keyService.decryptText(payload.get("bankAccountNumber").asText(),payload.get("customerId").asLong());
        customer.setBankAccountNumber(payload.get("bankAccountNumber").asText()); 
		
        //System.out.println("---------------------------->"+bankAccountNumber);

        //System.out.println("----->"+keyService.decryptText(payload.get("bankAccountNumber").asText(),payload.get("customerId").asLong()));
        customer.setIfscCode(payload.get("ifscCode").asText()); 
        customer.setAccountType(payload.get("accountType").asInt());
        
        System.out.println(customer);
        masterDataRepository.save(customer);

	// USECASE 5 REGISTER KEY FUNCTIONALITY
        keyController.registerKey(payload.get("customerId").asLong());

        return true; // Details updated
	}
	
	
	public Customer getCustomerProfile(Long customerId) {
		return masterDataRepository.findById(customerId).get();
	}
	
	public boolean checkEmailExists(JsonNode payload) {
		List<Customer> customers = masterDataRepository.findByEmail(payload.get("email").asText());
		return customers.isEmpty();
	}
	public boolean checkMobileNumberExists(JsonNode payload){
		List<Customer> customers = masterDataRepository.findByMobileNumber(payload.get("mobileNumber").asText());
		return customers.isEmpty();
	}
	public boolean checkBankAccountExists(JsonNode payload){
		return masterDataRepository.findByBankAccountNumber(payload.findValue("accountNumber").asText()).isEmpty();
	}
}
