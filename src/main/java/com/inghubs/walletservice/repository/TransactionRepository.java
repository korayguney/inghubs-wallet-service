package com.inghubs.walletservice.repository;

import com.inghubs.walletservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    Transaction findByWalletIdAndId(Long walletId, Long transactionId);
}