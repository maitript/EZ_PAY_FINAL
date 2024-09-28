package com.ezpay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**Author: Sandarbha Komujwar
 * Date:08/09/2024
 * Module:Password Recovery 
 */
/**
 * Entity class representing the password recovery details for a customer in the EzPay application.
 * This class contains information related to password recovery, such as the recovery token, its creation and expiration dates, 
 * and the status of the token's usage.
 */
@Entity
@Table(name = "password_recovery_details")
public class PasswordRecoveryDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_recovery_seq")
    @SequenceGenerator(name = "password_recovery_seq", sequenceName = "password_recovery_seq", allocationSize = 1)
    @Column(name = "recovery_id")
    private Long recoveryId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Links password recovery details to the customer entity

    @Column(name = "token", nullable = false, length = 255)
    private String token; // The unique token generated for password recovery

    @Column(name = "token_creation_date", nullable = false)
    private LocalDateTime tokenCreationDate; // The date and time when the token was created

    @Column(name = "token_expiration_date", nullable = false)
    private LocalDateTime tokenExpirationDate; // The date and time when the token will expire

    @Column(name = "is_token_used", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 0")
    private Boolean isTokenUsed; // Indicates whether the token has been used or not

    @Column(name = "password_reset_status", nullable = false)
    private Boolean passwordResetStatus; // 0 -> password failed to reset or 1 -> password successfully reset
    
    /**
     * Checks if the password reset token is expired by comparing the expiration date with the current date.
     *
     * @return true if the token has expired, false otherwise
     */
    public Boolean getPasswordResetStatus() {
        return passwordResetStatus;
    }

    public void setPasswordResetStatus(Boolean passwordResetStatus) {
        this.passwordResetStatus = passwordResetStatus;
    }
    
    public boolean isResetTokenExpired() {
        return tokenExpirationDate != null && tokenExpirationDate.isBefore(LocalDateTime.now());
    }

    // Getters and Setters
    public Long getRecoveryId() {
        return recoveryId;
    }

    public void setRecoveryId(Long recoveryId) {
        this.recoveryId = recoveryId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenCreationDate() {
        return tokenCreationDate;
    }

    public void setTokenCreationDate(LocalDateTime tokenCreationDate) {
        this.tokenCreationDate = tokenCreationDate;
    }

    public LocalDateTime getTokenExpirationDate() {
        return tokenExpirationDate;
    }

    public void setTokenExpirationDate(LocalDateTime tokenExpirationDate) {
        this.tokenExpirationDate = tokenExpirationDate;
    }

    public Boolean getIsTokenUsed() {
        return isTokenUsed;
    }

    public void setIsTokenUsed(Boolean isTokenUsed) {
        this.isTokenUsed = isTokenUsed;
    }

	
}
