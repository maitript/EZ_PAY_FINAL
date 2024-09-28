package com.ezpay.entity;
import java.sql.Date;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.*;

@Entity
@Table(name = "KMS", uniqueConstraints = @UniqueConstraint(columnNames = "customer_id"))
public class KeyManagement {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "encrypt_key")
    private String key;

    public KeyManagement() {
        super();
    }

    public KeyManagement(Customer customer, SecretKey secretKey, Date expiry_date) {
        super();
        this.customer = customer;
        this.key = encodeSecretKey(secretKey); // Encode key as Base64 String
        this.expiryDate = expiry_date;
    }

    public Customer getcustomer() {
        return customer;
    }

    public void setcustomer(Customer customer) {
        this.customer = customer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(SecretKey secretKey) {
        this.key = encodeSecretKey(secretKey); // Encode key as Base64 String
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiry_date) {
        this.expiryDate = expiry_date;
    }

    // Utility methods to encode/decode SecretKey
    public static String encodeSecretKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey decodeSecretKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, "AES");
    }
}
