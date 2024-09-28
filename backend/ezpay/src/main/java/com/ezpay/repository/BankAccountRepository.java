package com.ezpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezpay.entity.BankAccount;
import com.ezpay.entity.Customer;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    // Method to find by UPI ID
	//BankAccount findByUserUpiId(String upiId);

    // Method to find by bank account number
    BankAccount findByBankAccountNumber(String bankAccountNumber);

	BankAccount findByCustomer(Customer customer);
    
    //Retrieves a BankAccount by its account number and IFSC code.
    //BankAccount findByUserIfscCode(String ifscCode);
}
