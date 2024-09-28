package com.ezpay.service;

import com.ezpay.entity.Customer;
import com.ezpay.entity.KeyManagement;
import com.ezpay.repository.KeyRepository;
import com.ezpay.repository.MasterDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Base64;

@Service
public class KeyService {

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private MasterDataRepository customerRepository;

    public void registerKey(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Exception Occurred in UC5 KeyService RegisterKey: Customer not found"));
        SecretKey secretKey;
        
        KeyManagement existingKeyManagement = keyRepository.findByCustomerCustomerId(customerId);
        if (existingKeyManagement != null) {
            throw new RuntimeException("Exception Occurred in UC5 KeyService RegisterKey: Key already exists for the given customerId");
        }

		try {
			secretKey = generateSecretKey();
	        Date expiryDate = Date.valueOf(LocalDate.now().plusDays(30));

	        //saved the key in the database
	        KeyManagement keyManagement = new KeyManagement(customer, secretKey, expiryDate);
	        keyRepository.save(keyManagement);
	        
	        //setting the encrypted address and encrypted Account number into the database, since its the first time it is set like this
	        //String encryptedAddress = encrypt(customer.getAddress(),keyManagement.getKey());
	        //customer.setAddress(encryptedAddress);
	        
	        String encryptedAccNo= encrypt(customer.getBankAccountNumber(),keyManagement.getKey());
	        customer.setBankAccountNumber(encryptedAccNo);
	        customerRepository.save(customer);
	        
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Exception Occurred in UC5 KeyService RegisterKey: NoSuchAlgorithmException arised");
		}

    }
    
    
    public void updateCustomer(String oldKey, Long customerId)
    {
    	KeyManagement newKey = keyRepository.findByCustomerCustomerId(customerId);
    	Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Exception Occurred in UC5 KeyService RegisterKey: Customer not found"));
    	//String address = decrypt(customer.getAddress(),oldKey);
    	String accno= decrypt(customer.getBankAccountNumber(),oldKey);
    	
    	//customer.setAddress(encrypt(address,newKey.getKey()));
    	customer.setBankAccountNumber(encrypt(accno,newKey.getKey()));
    	
    	customerRepository.save(customer);
    	
    }
    
    public void updateFields(String oldKey, Long customerId)
    {
    	//This function whenever the key changes, so that the old text can be replaced with the new one.
    	//update in customer
    	updateCustomer(oldKey,customerId);
    	
    	//if in transaction the fields are set change that also
    	// updateUPITransaction(oldKey,customerId); //these can be added into those files if required
    	// updateBankAccount() etc etc 
    	//if in transaction uc3 some fields are set change that also
    	
    }

    public void updateKey(Long customerId) {
        KeyManagement keyManagement = keyRepository.findByCustomerCustomerId(customerId);
        
        // Case: Customer not found
        if (keyManagement == null) {
            throw new RuntimeException("Exception Occurred in UC5 KeyService updateKey: Customer not found");
        }

        // Case: Key is not expired yet (checkUpdate returns true)
        if (checkUpdate(customerId)) {
            System.out.println("Key isn't expired yet!");
            return; // Exit method, no further action needed
        }

        // Case: Key needs to be updated
        try {
            SecretKey secretKey = generateSecretKey();
            Date newExpiryDate = Date.valueOf(LocalDate.now().plusDays(90));
            String oldKey = keyManagement.getKey();
            keyManagement.setKey(secretKey);
            keyManagement.setExpiryDate(newExpiryDate);
            keyRepository.save(keyManagement);
            updateFields(oldKey, customerId);
            // Optionally return a success status or message
            // return 1;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Occurred in UC5 KeyService updateKey: NoSuchAlgorithmException occurred");
        }
    }
    
    
    
    public boolean checkUpdate(Long customerId) {
        KeyManagement keyManagement = keyRepository.findByCustomerCustomerId(customerId);
        if (keyManagement != null) {
            LocalDate currentDate = LocalDate.now();
            LocalDate expiryDate = keyManagement.getExpiryDate().toLocalDate();
            if (currentDate.isAfter(expiryDate)) {
                System.out.println("Password Expired, Reset Password to continue");
                return false;
            } else {
                System.out.println("Login Successful");
                return true;
            }
        }
        else {
        	throw new RuntimeException("Exception Occurred in UC5 KeyService checkUpdate:Customer not found");
        }
		
    }
    
    
    public String encrypt(String plainText, String key) {
        

     try {
        SecretKey secretKey = decodeSecretKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Exception Ocurred in UC5 KeyService Encrypt: Encrypting Algorith Error");
    }
}


    public String decrypt(String encryptedText, String key) {

            try {
            SecretKey secretKey = decodeSecretKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Ocurred in UC5 KeyService Decrypt:Decrypting Algorithm Error");
        }
    }

    public SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 256-bit AES key
        return keyGenerator.generateKey();
    }

    public String encodeSecretKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public SecretKey decodeSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, "AES");
    }


    
    
    /*This is only for checking if finaly it works no need for final functionality*/
	
	
	   public String encryptText(String plainText, Long customerId) {
	       
           KeyManagement keyManagement = keyRepository.findByCustomerCustomerId(customerId);
           if (keyManagement == null) {
               throw new RuntimeException("Exception Occurred in UC5 KeyService Encrypt:No key found for the given customerId");
           }
           
           return encrypt(plainText,keyManagement.getKey());
        
   }

   public String decryptText(String encryptedText, Long customerId) {
       		//System.out.println("------>   IN Decrpt");
           KeyManagement keyManagement = keyRepository.findByCustomerCustomerId(customerId);
           
           if (keyManagement == null) {
               throw new RuntimeException("Exception Ocurred in UC5 KeyService decrypt:No key found for the given customerId");
           }
           return decrypt(encryptedText,keyManagement.getKey());
          
   }
}
