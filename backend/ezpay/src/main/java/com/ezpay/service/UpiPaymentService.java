
package com.ezpay.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ezpay.auth.Authentication;
import com.ezpay.entity.BankAccount;
import com.ezpay.entity.Customer;
import com.ezpay.entity.UpiPayment;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.repository.UpiPaymentRepository;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UpiPaymentService {

    @Autowired
    private UpiPaymentRepository upiPaymentRepository;
    
    @Autowired
    private MasterDataRepository customerRepository;

    @Autowired
    private BankAccountService bankAccountService;

    public UpiPayment processPayment(String senderUpiId, String receiverUpiId, double amount, String label, String upiPin,String remark, Authentication auth) {
    	
    	Customer sender = customerRepository.findByUpiId(senderUpiId).orElse(null);
    	Customer receiver = customerRepository.findByUpiId(receiverUpiId).orElse(null);
    	
    	String transferId = "UPI" + generateRandomDigits(4) + "-" + generateRandomDigits(4) + "-" + generateRandomDigits(4) + "-" + generateRandomDigits(4);
    	
    	UpiPayment payment = new UpiPayment(
                transferId,
                senderUpiId,
                receiverUpiId,
                amount,
                4,
                label,
                LocalDateTime.now(),
                "UPI",
                remark,
                sender,
                receiver
            );
            upiPaymentRepository.save(payment);
            

        // Retrieve bank accounts for sender and receiver
        BankAccount senderAccount = bankAccountService.getBankAccountByUpiId(senderUpiId);
        BankAccount receiverAccount = bankAccountService.getBankAccountByUpiId(receiverUpiId);

        // Display message indicating payment initiation
        System.out.println("Payment initiated from " + senderUpiId + " to " + receiverUpiId + "...");

        // Simulate processing delay
        try {
            Thread.sleep(7000); // Simulate processing time with a 3-second delay
        } catch (InterruptedException e) {
            System.out.println("Payment processing interrupted.");
            return payment;
        }
        
     // Display message indicating payment initiation
        System.out.println("Payment Processing. Processing payment from " + senderUpiId + " to " + receiverUpiId + "...");
        
        
       
        // Update the existing payment record status
        payment.setStatus(3);
        upiPaymentRepository.save(payment);
        
        
     // Simulate further processing
        try {
            Thread.sleep(7000); // Simulate processing time with a 15-second delay
        } catch (InterruptedException e) {
            System.out.println("Payment processing interrupted.");
        }

        
        
      //check 1
        // Check if the session is active
//        if (!auth.isSessionActive()) {
//            System.out.println("Session has expired. Transaction aborted.");
//            payment.setStatus(0);
//            payment.setRemark("Session expired");
//            upiPaymentRepository.save(payment);
////            recordFailedPayment(senderUpiId, receiverUpiId, amount, "FAIL: Session expired", label);
//            return payment;
//        }
        
        //check 2
        // Check if the upi ids exist
        if (senderAccount == null || receiverAccount == null) {
            System.out.println("Invalid UPI ID(s). Transaction failed.");
            // Check if payment record already exists
          UpiPayment payment2 = upiPaymentRepository.findByPaymentId(payment.getPaymentId());
          
          if (payment2 == null) {
          	recordFailedPayment(senderUpiId, receiverUpiId, amount, 0, label,"ID(s) do not exist");
          } else {
              // Update the existing payment record status
              payment.setStatus(0);
              payment.setRemark("ID(s) do not exist");
              upiPaymentRepository.save(payment);
          }
            return payment;
        }
        
//        check 3
        // Check if sender has sufficient balance
        if (senderAccount.getBankAccountBalance() < amount) {
            System.out.println("Insufficient balance. Transaction failed.");
         // Check if payment record already exists
            UpiPayment payment2 = upiPaymentRepository.findByPaymentId(payment.getPaymentId());
            
            if (payment2 == null) {
            	recordFailedPayment(senderUpiId, receiverUpiId, amount, 0, label,"Insufficient Balance");
            } else {
                // Update the existing payment record status
                payment.setStatus(0);
                payment.setRemark("Insufficient Balance");
                upiPaymentRepository.save(payment);
            }
            
            return payment;
        }
        
//        check 4
        // Check if sender and receiver UPI IDs are the same
        if (senderUpiId.equals(receiverUpiId)) {
            System.out.println("Sender and receiver UPI IDs cannot be the same. Transaction failed.");
            // Check if payment record already exists
          UpiPayment payment2 = upiPaymentRepository.findByPaymentId(payment.getPaymentId());
          
          if (payment2 == null) {
          	recordFailedPayment(senderUpiId, receiverUpiId, amount, 0, label,"Same receiver and sender upi ids");
          } else {
              // Update the existing payment record status
              payment.setStatus(0);
              payment.setRemark("Sender and receiver UPI IDs cannot be the same");
              upiPaymentRepository.save(payment);
          }
            return payment;
        }

        //check 5
        // Verify the UPI PIN
   
        if (!bankAccountService.verifyUpiPin(senderUpiId, upiPin)) {
            System.out.println("Incorrect UPI PIN. Transaction failed.");
         // Check if payment record already exists
          UpiPayment payment2 = upiPaymentRepository.findByPaymentId(payment.getPaymentId());
          
          if (payment2 == null) {
          	recordFailedPayment(senderUpiId, receiverUpiId, amount, 0, label,"Incorrect upi pin entered");
          } else {
              // Update the existing payment record status
              payment.setStatus(0);
              payment.setRemark("Incorrect upi pin entered");
              upiPaymentRepository.save(payment);
          }
            return payment;
        }
        
        //check 6
//        fraud check
//        boolean
        // Deduct amount from sender and add to receiver
        double newSenderBalance = senderAccount.getBankAccountBalance() - amount;
        double newReceiverBalance = receiverAccount.getBankAccountBalance() + amount;

        bankAccountService.updateAccountBalance(senderAccount, newSenderBalance);
        bankAccountService.updateAccountBalance(receiverAccount, newReceiverBalance);

        
//        
        // Update the payment record to success or failure
        payment.setStatus(1); // or "FAILURE" based on your logic
        UpiPayment savedTransaction = upiPaymentRepository.save(payment);
        

        System.out.println("Transaction successful. Payment ID: " + payment.getPaymentId());
        this.processUpiTransaction(savedTransaction);
        System.out.println("The Email has been sent successfully please check that");
        return payment;
    }

   
    public void recordFailedPayment(String senderUpiId, String receiverUpiId, double amount, Integer failureReason, String label,String remark) {
    	Customer sender = customerRepository.findByUpiId(senderUpiId).orElse(null);
    	Customer receiver = customerRepository.findByUpiId(senderUpiId).orElse(null);
    	
        UpiPayment failedPayment = new UpiPayment(
            UUID.randomUUID().toString(),//UPI100-1234-1234-1234
            senderUpiId,
            receiverUpiId,
            amount,
            failureReason,
            label,
            LocalDateTime.now(),
            "UPI",
            remark,
            sender,
            receiver
        );
        upiPaymentRepository.save(failedPayment);
    }
    
    public Integer getPaymentStatus(String paymentId) {
        UpiPayment payment = upiPaymentRepository.findByPaymentId(paymentId);
        
        if (payment!=null) {

            return payment.getStatus();
        } else {
            return 5;
        }
    }
    
    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));  // Random digit from 0-9
        }
        return sb.toString();
    }
    
    
    // UC 6 CODE
    @Autowired
    private UpiEmailService emailService;
    public void processUpiTransaction(UpiPayment savedTransaction) {
        // Save the transaction as successful
//        transaction.setStatus("SUCCESS"); 
//    	UpiPayment savedTransaction = upiTransactionRepository.save(transaction);

        // Retrieve sender and receiver email from User repository
    	Customer sender = customerRepository.findByUpiId(savedTransaction.getSenderUpiId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Customer receiver = customerRepository.findByUpiId(savedTransaction.getReceiverUpiId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Send email to both sender and receiver
        try {
            emailService.sendTransactionSuccessEmail(sender.getEmail(), receiver.getEmail(), savedTransaction);
        } catch (MessagingException e) {
            // Handle the exception (e.g., log it)
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
    
    
    
    public Double getTotalUpiAmount(Long customerId) {
    	return upiPaymentRepository.getTotalAmountByCustomerId(customerId).orElse(0.0);
    }
    
    public int countUpiTransactionsForPreviousMonth(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfLastMonth = now.withDayOfMonth(1).minusDays(1).atTime(23, 59, 59);
        
        return upiPaymentRepository.countUpiPaymentsByUserIdAndPreviousMonth(userId, startOfLastMonth, endOfLastMonth);
    }
    
    //uc3 code:-
    
    /**
     * Retrieves UPI payments for a specific customer within a date range.
     * 
     * @param start      The start of the date range.
     * @param end        The end of the date range.
     * @param customerId The customer ID.
     * @return A list of UPI payments.
     */
    public List<UpiPayment> getPaymentsByDateRangeAndCustomerId(LocalDateTime start, LocalDateTime end, Long customerId) {
        return upiPaymentRepository.findByTimestampBetweenAndCustomerId(start, end, customerId);
    }

    public List<UpiPayment> getPaymentsByExactAmountAndCustomerId(double amount, Long customerId) {
        return upiPaymentRepository.findByAmountAndCustomerId(amount, customerId);
    }

    public List<UpiPayment> getPaymentsByCustomerId(Long customerId) {
    	return upiPaymentRepository.findByCustomerId(customerId); 
    }

    public List<UpiPayment> getPaymentsByMinAmountAndCustomerId(double minAmount, Long customerId) {
        return upiPaymentRepository.findByAmountGreaterThanEqualAndCustomerId(minAmount, customerId);
    }

    public List<UpiPayment> getPaymentsByMaxAmountAndCustomerId(double maxAmount, Long customerId) {
        return upiPaymentRepository.findByAmountLessThanEqualAndCustomerId(maxAmount, customerId);
    }

    public List<UpiPayment> getPaymentsByStatusAndCustomerId(Long status, Long customerId) {
        return upiPaymentRepository.findByStatusAndCustomerId(status, customerId);
    }

    public UpiPayment getPaymentByPaymentIdAndCustomerId(String paymentId, Long customerId) {
        return upiPaymentRepository.findByPaymentIdAndCustomerId(paymentId, customerId);
    }

	public List<UpiPayment> getTransactionsByDescriptionAndCustomerId(String descriptionKeyword, Long customerId) {
		return upiPaymentRepository.findByDescriptionContainingAndCustomerId(descriptionKeyword, customerId);
	} 
    
}

