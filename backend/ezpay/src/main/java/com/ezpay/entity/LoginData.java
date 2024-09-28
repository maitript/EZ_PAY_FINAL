package com.ezpay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

/**
 * Entity class representing the login data for a customer in the EzPay application.
 * Maps to the login_data table in the database.
 */
@Entity
@Table(name = "login_data")
public class LoginData {

    @Id
    @Column(name = "user_id", length = 30, unique=true) // Changed to String to match VARCHAR2(30)
    private String userId;

    @OneToOne
    @JoinColumn(name = "customer_id", unique = true, foreignKey = @ForeignKey(name = "FK_customer_login"))
    private Customer customer; // Links login data to the customer entity
	
    @ManyToOne
    @JoinColumn(name = "blocked_code", referencedColumnName="block_id",nullable = false)
    private SuspiciousActivity suspiciousActivity;

    
     public SuspiciousActivity getSuspiciousActivity() {
	     return suspiciousActivity;
	}
	
	public void setSuspiciousActivity(SuspiciousActivity suspiciousActivity) {
		this.suspiciousActivity = suspiciousActivity;
	}


	@Column(name = "password_hash", nullable = false)
    private String passwordHash; // Changed to byte[] to match RAW(64)

    
    
    

	public LoginData() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

   public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
