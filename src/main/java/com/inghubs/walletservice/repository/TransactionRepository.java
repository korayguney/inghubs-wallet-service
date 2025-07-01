package com.inghubs.walletservice.repository;

import com.inghubs.walletservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletId(Long walletId);

    Transaction findByWalletIdAndId(Long walletId, Long transactionId);
}