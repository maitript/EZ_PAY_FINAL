package com.ezpay.service;

import com.ezpay.entity.Customer;

import com.ezpay.entity.FraudEntry;
import com.ezpay.entity.LoginData;
import com.ezpay.entity.SuspiciousActivity;
import com.ezpay.repository.FraudEntryRepository;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.repository.SuspiciousActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

//Preconditions
//Should already ensure that the customer id already exists !! this check to be done by UC2 before passing it here

@Service
public class FraudDetectionService {

    @Autowired
    private FraudEntryRepository fraudEntryRepository;

    @Autowired
    private MasterDataRepository customerRepository;
    
    @Autowired
    private LoginDataRepository loginInfoRepository;

    @Autowired
    private SuspiciousActivityRepository suspiciousActivityRepository;

    

    // Method to flag a login attempt based on customerId
    public boolean flagLoginAttempt(Long customerId) {
        try {
            // Insert fraud entry for login attempt
            insertFraudEntry(customerId, 1, 0);

            // Retrieve the risk counts
            int actual1 = suspiciousActivityRepository.getActualRiskCount(1);
            int countRisk1 = fraudEntryRepository.getCountRisk(customerId, 1);
            System.out.println("Login Risk Count: " + countRisk1 + "/" + actual1);

            // Check if the login attempts exceed the threshold
            if (actual1 <= countRisk1) {
                updateLoginDetails(customerId, 1);
                return true; // Indicate that the user should be blocked
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
        return false; // Indicate that the user should not be blocked
    }

    // Method to flag a transaction based on customerId
    /*public boolean flagTransactionForToken(Integer customerId) {
        try {
            // Get the number of successful transactions for the customer
            int transactionCount = transactionRepository.getTransactionCount(customerId);
            System.out.println("transactionCount="+transactionCount);
            
            // Only proceed if the customer has more than 3 transactions (to get the pattern)
            if (transactionCount >= 3) {
                // Calculate average successful transaction amount and the last initiated transaction amount
            	//System.out.println("No problem here!");
                double avgAmount = transactionRepository.getAverageTransactionAmount(customerId);
            	//System.out.println("No problem here as well!");

                double transactionAmount = transactionRepository.getLastTransactionAmount(customerId);
            	//System.out.println(" problem ??!");

                System.out.println("average amount="+avgAmount+"\ntransactionAmount="+transactionAmount);
                if (transactionAmount > avgAmount) {
                    // Insert fraud entry for high transaction
                    //insertFraudEntry(customerId, 2, 0);
                		return true;
                	
                    // Retrieve the risk counts
                    //int actual2 = suspiciousActivityRepository.getActualRiskCount(2);
                    //int countRisk2 = fraudEntryRepository.getCountRisk(customerId, 2);
                    //System.out.println("Login Risk Count: " + countRisk2 + "/" + actual2);

                    // Check if the transaction amounts exceed the threshold
                    //if (actual2 <= countRisk2) {
                      //  updateLoginDetails(customerId, 2);
                       // return true;
                    //}
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        } 
        return false; // Return the result to the controller
    }
    
    
    public boolean flagTransaction(Integer customerId) {
        // Insert fraud entry for high transaction
        try {
            // Insert a fraud entry with risk type 2
            insertFraudEntry(customerId, 2, 0);
            
            // Get actual risk count and count of fraud entries
            int actual2 = suspiciousActivityRepository.getActualRiskCount(2);
            int countRisk2 = fraudEntryRepository.getCountRisk(customerId, 2);
            System.out.println("Login Risk Count: " + countRisk2 + "/" + actual2);

            // Check if the transaction amounts exceed the threshold
            if (actual2 <= countRisk2) {
                // Update login details if necessary
                updateLoginDetails(customerId, 2);

                // Get the transaction associated with the customerId
                Transaction transaction = transactionRepository.findTransactionByCustomerId(customerId);
                if (transaction != null) {
                    // Change transaction status to "failed"
                    transaction.setStatus("failed");
                    transactionRepository.save(transaction); // Persist the changes
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
        return false;
    }


*/
    // Insert a new fraud entry in the database
    private void insertFraudEntry(Long customerId, int blockId, int resolved) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        SuspiciousActivity suspiciousActivity = suspiciousActivityRepository.findById(blockId).orElse(null);

        if (customer != null && suspiciousActivity != null) {
            FraudEntry fraudEntry = new FraudEntry();
            fraudEntry.setCustomer(customer);
            fraudEntry.setSuspiciousActivity(suspiciousActivity);
            fraudEntry.setDateOfEntry(new Timestamp(System.currentTimeMillis()));
            fraudEntry.setResolved(resolved);

            fraudEntryRepository.save(fraudEntry);
        }
    }

    // Update login details to block a user
    private void updateLoginDetails(Long customerId, int blockId) {
        LoginData loginDetail = loginInfoRepository.findByCustomerCustomerId(customerId);
        SuspiciousActivity suspiciousActivity = suspiciousActivityRepository.findById(blockId).orElse(null);

        if (loginDetail != null && suspiciousActivity != null) {
        	System.out.println("I am updating though");
            loginDetail.setSuspiciousActivity(suspiciousActivity);
            loginInfoRepository.save(loginDetail); // Update the login detail to block the user
        }
    }

}
