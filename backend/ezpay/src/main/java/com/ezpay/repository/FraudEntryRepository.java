package com.ezpay.repository;

import com.ezpay.entity.FraudEntry;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudEntryRepository extends JpaRepository<FraudEntry, Integer> {

    @Query("SELECT COUNT(*) FROM FraudEntry fe WHERE fe.customer.customerId = :customerId AND fe.suspiciousActivity.blockId = :blockId AND fe.resolved = 0")
    int getCountRisk(@Param("customerId") Long customerId, @Param("blockId") int blockId);
    
    //update fraud_entries set blocked_
    //@Query("")
    
    @Modifying
    @Transactional
    @Query("UPDATE FraudEntry fe SET fe.resolved = 1 WHERE fe.customer.customerId = :customerId AND fe.suspiciousActivity.blockId = 1")
    int updateResolvedForCustomer(Long customerId);
    
}
