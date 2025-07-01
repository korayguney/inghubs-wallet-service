package com.inghubs.walletservice.repository;

import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.criteria.WalletRepositoryCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, WalletRepositoryCriteria {
}