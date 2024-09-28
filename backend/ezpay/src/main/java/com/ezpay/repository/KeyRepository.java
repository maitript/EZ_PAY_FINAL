package com.ezpay.repository;

import com.ezpay.entity.KeyManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyRepository extends JpaRepository<KeyManagement, Integer> {
    KeyManagement findByCustomerCustomerId(Long customerId);
}
