package com.inghubs.walletservice.repository.criteria;

import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.model.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;


public interface WalletRepositoryCriteria {
    List<Wallet> findWalletsByFilters(Long customerId, Currency currency, BigDecimal minAmount, BigDecimal maxAmount);
}