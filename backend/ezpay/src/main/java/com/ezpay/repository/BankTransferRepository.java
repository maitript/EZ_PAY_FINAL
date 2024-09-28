package com.ezpay.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ezpay.entity.BankTransfer;

@Repository
public interface BankTransferRepository extends JpaRepository<BankTransfer, String>{
	// Define custom query methods if needed
	BankTransfer findByTransferId(String transferId);
	
	//uc 6 code
	@Query("SELECT SUM(b.amount) FROM BankTransfer b WHERE b.sender.customerId = :customerId")
	Optional<Double> getTotalAmountByCustomerId(@Param("customerId") Long customerId);
	
	
	@Query("SELECT COUNT(b) FROM BankTransfer b WHERE b.sender.customerId = :customerId AND b.timestamp BETWEEN :startDate AND :endDate")
    int countBankTransfersByUserIdAndPreviousMonth(@Param("customerId") Long customerId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
	
	//uc3 code:- 
	
	@Query("SELECT bt FROM BankTransfer bt WHERE bt.timestamp BETWEEN :start AND :end AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByTimestampBetweenAndCustomerId(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE FLOOR(bt.amount) = FLOOR(:amount) AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByAmountAndCustomerId(
        @Param("amount") double amount,
        @Param("customerId") Long customerId
    );


    @Query("SELECT bt FROM BankTransfer bt WHERE bt.amount >= :minAmount AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByAmountGreaterThanEqualAndCustomerId(
            @Param("minAmount") double minAmount,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE bt.amount <= :maxAmount AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByAmountLessThanEqualAndCustomerId(
            @Param("maxAmount") double maxAmount,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT bt FROM BankTransfer bt WHERE bt.status = :status AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByStatusAndCustomerId(
            @Param("status") Long status,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE LOWER(bt.label) LIKE LOWER(CONCAT('%', :descriptionKeyword, '%')) AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    List<BankTransfer> findByDescriptionContainingAndCustomerId(
            @Param("descriptionKeyword") String descriptionKeyword,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE bt.transferId = :transactionId AND (bt.sender.customerId = :customerId OR bt.receiver.customerId = :customerId)")
    BankTransfer findByTransferIdAndCustomerId(
            @Param("transactionId") String transactionId,
            @Param("customerId") Long customerId
    );

    @Query("SELECT bt FROM BankTransfer bt WHERE bt.sender.customerId = :customerId")
    List<BankTransfer> findBySenderCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT bt FROM BankTransfer bt WHERE bt.receiver.customerId = :customerId")
    List<BankTransfer> findByReceiverCustomerId(@Param("customerId") Long customerId);

}
