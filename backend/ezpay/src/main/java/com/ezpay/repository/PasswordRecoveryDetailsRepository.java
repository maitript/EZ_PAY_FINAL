package com.ezpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezpay.entity.Customer;
import com.ezpay.entity.PasswordRecoveryDetails;

import java.util.List;
import java.util.Optional;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Repository interface for handling database operations related to the PasswordRecoveryDetails entity.
 * Extends JpaRepository to provide standard CRUD operations for PasswordRecoveryDetails.
 */
@Repository
public interface PasswordRecoveryDetailsRepository extends JpaRepository<PasswordRecoveryDetails, Long> {
	 /**
     * Finds the password recovery details by the reset token.
     *
     * @param token the password reset token
     * @return an Optional containing the PasswordRecoveryDetails if found, or an empty Optional if no details are found
     */
    Optional<PasswordRecoveryDetails> findByToken(String token);
    /**
     * Finds the password recovery details associated with a specific customer.
     *
     * @param customer the customer whose password recovery details are being searched
     * @return an Optional containing the PasswordRecoveryDetails if found, or an empty Optional if no details are found
     */
    List<PasswordRecoveryDetails> findByCustomer(Customer customer);
}
