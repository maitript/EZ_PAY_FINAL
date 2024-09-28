package com.ezpay.repository;

/**
 * @Author Sagar Kumar
 * @Purpose Created to use JPA CRUD feature
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezpay.entity.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterDataRepository extends JpaRepository<Customer, Long> {
	List<Customer> findByEmail(String email);
	
	List<Customer> findByMobileNumber(String mobileNumber);
	//List<Customer> findByBankAccountNumber(String bankAccountNumber);
	 
    Optional<Customer> findByUpiId(String upiId);
 
    Optional<Customer> findByBankAccountNumberAndIfscCode(String accountNumber, String ifscCode);
    
    Optional<Customer> findByBankAccountNumber(String accountNumber);
    
    Optional<Customer> findByCustomerId(int customerId);
}
