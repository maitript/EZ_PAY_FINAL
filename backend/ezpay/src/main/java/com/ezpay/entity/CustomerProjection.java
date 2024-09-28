package com.ezpay.entity;


//DTO projection from Customer entity to use only those members which can be updated
public interface CustomerProjection {
	int getCustomerId();
  String getName();
  String getEmail();
  String getMobileNumber();
  String getAddress();
  String getIfscCode();
  String getAccNo();
  String getDob();
  String getGender();
  String getProfilePictureUrl();
  int getAccountType();
}
