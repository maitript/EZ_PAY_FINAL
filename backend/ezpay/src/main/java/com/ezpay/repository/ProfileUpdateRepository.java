package com.ezpay.repository;


//import java.util.List;


import java.util.Optional;
//import com.ezpay.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.transaction.annotation.Transactional;

import com.ezpay.entity.*;
//Modify ProfileManagementRepository to return Optional<Customer>
public interface ProfileUpdateRepository extends JpaRepository<Customer,Integer>{
	

	   // @Modifying
	    //@Transactional
	    //@Query("UPDATE Customer c SET c.name = :name, c.email = :email, c.mobileNumber = :mobileNumber, c.address = :address, c.ifscCode = :ifscCode, c.accNo = :accNo, c.gender = :gender, c.dob = :dob, c.accType = :accType, c.profilePictureUrl = :profilePicturUrl  WHERE c.customerId = :customerId")
	   // int updateCustomerDetails(int customerId, String name, String email, String mobileNumber, String address, String ifscCode, String accNo,String gender,String dob,int accType,String profilePictureUrl);

	Optional<Customer> findByCustomerId(Long customerId);
	
	 // Check if email exists for a different customer
    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);

    // Check if mobile number exists for a different customer
    boolean existsByMobileNumberAndCustomerIdNot(String mobileNumber, Long customerId);
	    
	    
	}
