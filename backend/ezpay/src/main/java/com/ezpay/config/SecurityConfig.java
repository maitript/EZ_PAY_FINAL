package com.ezpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Configuration class for setting up security features in the application.
 * It defines password encoding and configures security rules for HTTP requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	 /**
     * Configures and returns a BCryptPasswordEncoder bean for password encryption.
     *
     * @return a PasswordEncoder instance that uses the BCrypt hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    	// Business Logic: Use BCryptPasswordEncoder for secure password hashing
        return new BCryptPasswordEncoder();
    }
    /**
     * Configures the security filter chain to manage access to various HTTP endpoints.
     *
     * @param http the HttpSecurity object used to configure security settings
     * @return a SecurityFilterChain that defines the security configuration for HTTP requests
     * @throws Exception if there is an error configuring the security chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	  // Business Logic: Disable CSRF protection for non-browser clients and permit access to password recovery endpoints
        http
        //Adding this for Profile Management ease of routing
            .cors()
            .and()
            .csrf().disable()  // Disable CSRF protection for non-browser clients
            .authorizeRequests()
            	.requestMatchers("/api/check_user_id","/api/check_if_email_present", "/api/check_if_mobile_present","/api/check_if_bankacc_present").permitAll()
            	.requestMatchers("/api/login", "/api/register-user").permitAll()	//Allow access to Login APIs
                .requestMatchers("/api/password/forgot", "/api/password/reset").permitAll() // Allow access to password recovery APIs
                //Allow access for profile updates
                .requestMatchers("/customers/by-id/**").permitAll()
                .requestMatchers("/customers/update").permitAll()
                .requestMatchers("/customers/verify-password").permitAll()
                .requestMatchers("/api/add-profile-details", "/api/view-profile","/customers/update").permitAll()	//Allow access to registration APIs
                .anyRequest().authenticated()  // All other endpoints require authentication
            .and()
            .formLogin().disable();  // Disable form-based login, as it's not needed for this configuration

        return http.build();
    }
}
