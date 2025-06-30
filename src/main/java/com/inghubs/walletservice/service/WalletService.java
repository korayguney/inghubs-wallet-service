package com.inghubs.walletservice.service;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.PaymentResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    CreateWalletResponse createWallet(CreateWalletRequest request);

    List<CreateWalletResponse> listWallets(Long customerId, Currency currency, BigDecimal minAmount, BigDecimal maxAmount);

    PaymentResponse makeDeposit(PaymentRequest request);

    PaymentResponse makeWithdraw(PaymentRequest request);
}
