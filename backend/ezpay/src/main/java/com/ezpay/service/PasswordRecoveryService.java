package com.ezpay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ezpay.entity.Customer;
import com.ezpay.entity.LoginData;
import com.ezpay.entity.PasswordRecoveryDetails;
import com.ezpay.entity.SuspiciousActivity;
import com.ezpay.repository.FraudEntryRepository;
import com.ezpay.repository.LoginDataRepository;
import com.ezpay.repository.MasterDataRepository;
import com.ezpay.repository.PasswordRecoveryDetailsRepository;
import com.ezpay.repository.SuspiciousActivityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Service class to handle password recovery operations like creating a reset
 * token, sending emails, and resetting the password.
 */

@Service
public class PasswordRecoveryService {

	@Autowired
	private MasterDataRepository masterDataRepository;
	
	@Autowired
	private SuspiciousActivityRepository suspiciousActivityRepository;

	@Autowired
	private PasswordRecoveryDetailsRepository passwordRecoveryDetailsRepository;

	@Autowired
	private LoginDataRepository loginDataRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	FraudEntryRepository fraudEntryRepository;

	@Value("${server.port}")
	private String port;
	
	private String baseUrl="http://localhost:"+port;

	@Value("${spring.web.cors.allowed-origin-patterns}")
	private String reactBaseUrl;

	/**
	 * Creates a password reset token for the customer identified by the provided
	 * email or mobile number. Each request creates a new entry in the
	 * PasswordRecoveryDetails table.
	 *
	 * @param emailOrMobile the registered email or mobile number of the customer
	 * @return a message indicating that the reset link has been sent, or an error
	 *         if the account is not found
	 */
	public String createPasswordResetToken(String emailOrMobile) {
		List<Customer> customersByEmail = masterDataRepository.findByEmail(emailOrMobile);
		List<Customer> customersByMobile = masterDataRepository.findByMobileNumber(emailOrMobile);

		Optional<Customer> customerOptional = Stream.concat(customersByEmail.stream(), customersByMobile.stream())
		    .findFirst();

		if (customerOptional.isPresent()) {
			Customer customer = customerOptional.get();

			// Fetch all existing tokens for this customer
			List<PasswordRecoveryDetails> existingTokens = passwordRecoveryDetailsRepository.findByCustomer(customer);

			PasswordRecoveryDetails recoveryDetails = null;
			boolean newTokenNeeded = true;

			// Iterate through the existing tokens and check their status
			for (PasswordRecoveryDetails token : existingTokens) {
				if (!token.isResetTokenExpired() && !token.getIsTokenUsed()) {
					// If a valid token exists, use it
					newTokenNeeded = false;
					recoveryDetails = token;
					return "Password reset link has already been sent to " + customer.getEmail();
				}
			}

			if (newTokenNeeded) {
				// Create a new token if no valid tokens are found
				recoveryDetails = new PasswordRecoveryDetails();
				recoveryDetails.setCustomer(customer);
				recoveryDetails.setToken(UUID.randomUUID().toString());
				recoveryDetails.setTokenCreationDate(LocalDateTime.now());
				recoveryDetails.setTokenExpirationDate(LocalDateTime.now().plusMinutes(10)); // Token valid for 10
																								// minutes
				recoveryDetails.setIsTokenUsed(false);
				recoveryDetails.setPasswordResetStatus(false); // Password reset not yet successful
				passwordRecoveryDetailsRepository.save(recoveryDetails);
			}

			// Send reset link via email
			sendPasswordResetEmail(customer.getEmail(), recoveryDetails.getToken());

			return "Password reset link sent to " + customer.getEmail();
		} else {
			throw new IllegalArgumentException("No account found with the provided email or mobile number.");
		}
	}

	/**
	 * Sends a password reset email to the user with the provided email address.
	 *
	 * @param recipientEmail the email address of the recipient
	 * @param token          the password reset token to be included in the email
	 */
	private void sendPasswordResetEmail(String recipientEmail, String token) {
		String resetLink = baseUrl + "/api/password/reset?token=" + token;
		String resetReactLink = reactBaseUrl + "/password/reset?token=" + token;
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(recipientEmail);
		message.setSubject("Password Reset Request");
		message.setText("Click the link to reset your password: " + resetReactLink);
		message.setFrom("no-reply@ezpay.com");

		mailSender.send(message);
	}

	/**
	 * Validates the provided password reset token and resets the user's password.
	 * Updates the password recovery details with the password reset status.
	 *
	 * @param token       the password reset token
	 * @param newPassword the new password to set for the user
	 * @return a message indicating the result of the password reset operation
	 */
	public String resetPassword(String token, String newPassword) {
		Optional<PasswordRecoveryDetails> recoveryDetailsOptional = passwordRecoveryDetailsRepository
				.findByToken(token);

		if (recoveryDetailsOptional.isPresent()) {
			PasswordRecoveryDetails recoveryDetails = recoveryDetailsOptional.get();

			// Check if the token has expired
			if (recoveryDetails.isResetTokenExpired()) {
				throw new IllegalArgumentException("The reset link has expired.");
			}

			// Check if the token has already been used
			if (recoveryDetails.getIsTokenUsed()) {
				throw new IllegalArgumentException("The reset link has already been used.");
			}

			Customer customer = recoveryDetails.getCustomer();

			// Fetch LoginData using customer reference
			Optional<LoginData> loginDataOptional = loginDataRepository.findByCustomer(customer);

			if (loginDataOptional.isPresent()) {
				LoginData loginData = loginDataOptional.get();
				// Encrypt and set the new password for the customer
				loginData.setPasswordHash(passwordEncoder.encode(newPassword));
				  SuspiciousActivity sus = suspiciousActivityRepository.findById(0).orElse(null);
		        	loginData.setSuspiciousActivity(sus); // Reset blockedCode after password reset

				//loginData.setBlockedCode(0); // Reset blockedCode after password reset
				loginDataRepository.save(loginData); // Save the updated password

				//UseCase 5 functionality to set the values in fraudEntries to 1 where customer_id= loginData.Customer.customerId and loginData.suspiciousactivity.blockedCode=1
				fraudEntryRepository.updateResolvedForCustomer(loginData.getCustomer().getCustomerId());

				// Update the token's status to indicate it has been used and password reset is
				// successful
				recoveryDetails.setIsTokenUsed(true);
				recoveryDetails.setPasswordResetStatus(true); // Password reset was successful
				passwordRecoveryDetailsRepository.save(recoveryDetails);

				return "Password reset successfully.";
			} else {
				throw new IllegalArgumentException("No login data found for the user.");
			}
		} else {
			throw new IllegalArgumentException("Invalid password reset token.");
		}
	}
}
