package com.ezpay.entity;

//import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Entity class representing a customer in the EzPay application. This class
 * contains various attributes related to customer information, such as name,
 * contact details, account information, and profile metadata.
 */
@Entity
@Table(name = "master_data") // Mapping to the "master_data" table
public class Customer {
    public Customer() {
		super();
		}

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_seq") // Using SEQUENCE instead of IDENTITY to match SQL
    @SequenceGenerator(name = "customer_id_seq", sequenceName = "customer_id_seq", allocationSize = 1)
	@Column(name = "customer_id") // Mapping customerId to "customer_id"
    private Long customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_number", nullable = false, unique = true) // Mapping mobileNumber to "mobile_number"
    private String mobileNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "dob", nullable = false)
    private LocalDateTime dob;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "profile_creation_date", nullable = false) // Mapping profileCreationDate to "profile_creation_date"
    private LocalDateTime profileCreationDate;

    @Column(name = "profile_last_updated_date", nullable = false) // Mapping profileLastUpdatedDate to "profile_last_updated_date"
    private LocalDateTime profileLastUpdatedDate;

    @Column(name = "profile_picture_URL") // Mapping profilePictureUrl to "profile_picture_URL"
    private String profilePictureUrl; // Changed to byte[] for BLOB data type

    @Column(name = "is_profile_info_set", nullable = false) // Mapping isProfileInfoSet to "is_profile_info_set"
    private Boolean isProfileInfoSet;

    @Column(name = "upi_id", nullable = false, unique = true) // Mapping upiId to "upi_id"
    private String upiId;

    @Column(name = "bank_account_number", nullable = false, unique = true) // Mapping accNo to "bank_account_number"
    private String bankAccountNumber;

    @Column(name = "ifsc_code", nullable = false) // Mapping ifscCode to "ifsc_code"
    private String ifscCode;

    @Column(name = "account_type", nullable = false) // Added accountType field to match SQL
    private Integer accountType;
	
    

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	// Getters and Setters
	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDateTime getDob() {
		return dob;
	}

	public void setDob(LocalDateTime dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDateTime getProfileCreationDate() {
		return profileCreationDate;
	}

	public void setProfileCreationDate(LocalDateTime profileCreationDate) {
		this.profileCreationDate = profileCreationDate;
	}

	public LocalDateTime getProfileLastUpdatedDate() {
		return profileLastUpdatedDate;
	}

	public void setProfileLastUpdatedDate(LocalDateTime profileLastUpdatedDate) {
		this.profileLastUpdatedDate = profileLastUpdatedDate;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public Boolean getIsProfileInfoSet() {
		return isProfileInfoSet;
	}

	public void setIsProfileInfoSet(Boolean isProfileInfoSet) {
		this.isProfileInfoSet = isProfileInfoSet;
	}

	public String getUpiId() {
		return upiId;
	}

	public void setUpiId(String upiId) {
		this.upiId = upiId;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", name=" + name + ", email=" + email + ", mobileNumber="
				+ mobileNumber + ", address=" + address + ", dob=" + dob + ", gender=" + gender
				+ ", profileCreationDate=" + profileCreationDate + ", profileLastUpdatedDate=" + profileLastUpdatedDate
				+ ", profilePictureUrl=" + profilePictureUrl + ", isProfileInfoSet=" + isProfileInfoSet
				+ ", upiId=" + upiId + ", bankAccountNumber=" + bankAccountNumber + ", ifscCode=" + ifscCode
				+ ", accountType=" + accountType + "]";
	}
	

}