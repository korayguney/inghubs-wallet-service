package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.WalletTransactionException;
import com.inghubs.walletservice.mapper.WalletMapper;
import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.PaymentResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import com.inghubs.walletservice.model.entity.Customer;
import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.WalletRepository;
import com.inghubs.walletservice.service.TransactionService;
import com.inghubs.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;

    @Override
    public CreateWalletResponse createWallet(CreateWalletRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCustomer(customer);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        Wallet saved = walletRepository.save(wallet);
        return walletMapper.toResponse(saved);
    }

    @Override
    public List<CreateWalletResponse> listWallets(Long customerId, Currency currency, BigDecimal minAmount, BigDecimal maxAmount) {
        List<Wallet> wallets = walletRepository.findWalletsByFilters(customerId, currency, minAmount, maxAmount);
        return wallets.stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse makeDeposit(PaymentRequest request) {
        return processPayment(request, TransactionType.DEPOSIT, true);
    }

    @Override
    public PaymentResponse makeWithdraw(PaymentRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletTransactionException("Wallet not found"));

        if (!wallet.isActiveForWithdraw()) {
            throw new WalletTransactionException("Withdraw is not allowed for this wallet");
        }

        return processPayment(wallet, request, TransactionType.WITHDRAW, false);
    }

    private PaymentResponse processPayment(PaymentRequest request, TransactionType type, boolean isAddition) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletTransactionException("Wallet not found"));

        return processPayment(wallet, request, type, isAddition);
    }

    private PaymentResponse processPayment(Wallet wallet, PaymentRequest request, TransactionType type, boolean isAddition) {
        TransactionStatus transactionStatus;

        if (request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            transactionStatus = TransactionStatus.PENDING;
        } else {
            transactionStatus = TransactionStatus.APPROVED;
        }

        BigDecimal amount = request.getAmount();

        if (isAddition) {
            wallet.setBalance(wallet.getBalance().add(amount));
            if (transactionStatus == TransactionStatus.APPROVED) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(amount));
            }
        } else {
            wallet.setBalance(wallet.getBalance().subtract(amount));
            if (transactionStatus == TransactionStatus.APPROVED) {
                wallet.setUsableBalance(wallet.getUsableBalance().subtract(amount));
            }
        }

        transactionService.processTransaction(wallet, request, transactionStatus, type);
        walletRepository.save(wallet);

        return PaymentResponse.builder()
                .walletId(wallet.getId())
                .currency(wallet.getCurrency().name())
                .totalBalance(wallet.getBalance())
                .usableBalance(wallet.getUsableBalance())
                .transactionStatus(transactionStatus.name())
                .build();
    }

}