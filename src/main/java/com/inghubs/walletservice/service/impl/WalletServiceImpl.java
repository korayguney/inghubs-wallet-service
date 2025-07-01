package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.*;
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

/**
 * Implementation of the WalletService interface.
 * Provides methods for managing wallets and processing payments.
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;

    /**
     * Creates a new wallet for a customer.
     *
     * @param request The request containing wallet details and customer ID.
     * @return The response containing the created wallet details.
     * @throws CustomerNotFoundException If the customer is not found.
     */
    @Override
    public CreateWalletResponse createWallet(CreateWalletRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCustomer(customer);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        Wallet saved = walletRepository.save(wallet);
        return walletMapper.toResponse(saved);
    }

    /**
     * Lists wallets based on filters such as customer ID, currency, and balance range.
     *
     * @param customerId The ID of the customer.
     * @param currency The currency of the wallets.
     * @param minAmount The minimum balance of the wallets.
     * @param maxAmount The maximum balance of the wallets.
     * @return A list of wallet responses matching the filters.
     */
    @Override
    public List<CreateWalletResponse> listWallets(Long customerId, Currency currency, BigDecimal minAmount, BigDecimal maxAmount) {
        List<Wallet> wallets = walletRepository.findWalletsByFilters(customerId, currency, minAmount, maxAmount);
        return wallets.stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    /**
     * Processes a deposit transaction for a wallet.
     *
     * @param request The payment request containing wallet ID and amount.
     * @return The response containing updated wallet details and transaction status.
     */
    @Override
    public PaymentResponse makeDeposit(PaymentRequest request) {
        return processPayment(request, TransactionType.DEPOSIT, true);
    }

    /**
     * Processes a withdrawal transaction for a wallet.
     *
     * @param request The payment request containing wallet ID and amount.
     * @return The response containing updated wallet details and transaction status.
     * @throws WalletNotFoundException If the wallet is not found.
     * @throws WithdrawNotAllowedException If the withdrawal is not allowed.
     * @throws InsufficientBalanceException If the wallet has insufficient balance for withdrawal.
     */
    @Override
    public PaymentResponse makeWithdraw(PaymentRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (!wallet.isActiveForWithdraw()) {
            throw new WithdrawNotAllowedException("Withdraw is not allowed for this wallet");
        }

        if (wallet.getUsableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        return processPayment(wallet, request, TransactionType.WITHDRAW, false);
    }

    /**
     * Processes a payment transaction for a wallet.
     *
     * @param request The payment request containing wallet ID and amount.
     * @param type The type of transaction (DEPOSIT or WITHDRAW).
     * @param isAddition Indicates whether the transaction adds to the balance (true for deposit, false for withdraw).
     * @return The response containing updated wallet details and transaction status.
     * @throws InvalidPaymentAmountException If the payment amount is invalid (less than or equal to zero).
     * @throws WalletNotFoundException If the wallet is not found.
     */
    private PaymentResponse processPayment(PaymentRequest request, TransactionType type, boolean isAddition) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException("Payment amount must be greater than zero.");
        }

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        return processPayment(wallet, request, type, isAddition);
    }

    /**
     * Processes a payment transaction for a wallet with additional details.
     *
     * @param wallet The wallet associated with the transaction.
     * @param request The payment request containing transaction details.
     * @param type The type of transaction (DEPOSIT or WITHDRAW).
     * @param isAddition Indicates whether the transaction adds to the balance (true for deposit, false for withdraw).
     * @return The response containing updated wallet details and transaction status.
     */
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

    /**
     * Checks if a wallet is owned by a specific customer.
     *
     * @param walletId The ID of the wallet.
     * @param customerId The ID of the customer.
     * @return True if the wallet is owned by the customer, false otherwise.
     */
    public boolean isWalletOwnedBy(Long walletId, Long customerId) {
        return walletRepository.findById(walletId)
                .map(w -> w.getCustomer().getId().equals(customerId))
                .orElse(false);
    }

}
