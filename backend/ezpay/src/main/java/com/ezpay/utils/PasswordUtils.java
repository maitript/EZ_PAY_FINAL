package com.ezpay.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Riya Shah
 * Utility class for handling password hashing and verification using BCrypt.
 */
public class PasswordUtils {

	// Instance of BCryptPasswordEncoder used for hashing and verifying passwords
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	/**
	 * Hashes the given plaintext password using BCrypt encryption.
	 * 
	 * @param password The plaintext password to hash.
	 * @return The hashed password.
	 */
	public static String hashPassword(String password) {
		// Encrypt the plaintext password and return the hashed version
		return encoder.encode(password);
	}

	/**
	 * Verifies a plaintext password against a hashed password.
	 * 
	 * @param password       The plaintext password to check.
	 * @param hashedPassword The hashed password to compare against.
	 * @return True if the plaintext password matches the hashed password, false
	 *         otherwise.
	 */
	public static boolean verifyPassword(String password, String hashedPassword) {
		// Check if the provided password matches the hashed password
		return encoder.matches(password, hashedPassword);
	}
}
