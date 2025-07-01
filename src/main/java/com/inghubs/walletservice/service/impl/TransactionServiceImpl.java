package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.WalletNotFoundException;
import com.inghubs.walletservice.mapper.TransactionMapper;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import com.inghubs.walletservice.model.entity.Transaction;
import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.TransactionRepository;
import com.inghubs.walletservice.repository.WalletRepository;
import com.inghubs.walletservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the TransactionService interface.
 * Provides methods for managing transactions and wallets.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Creates a new transaction and saves it to the repository.
     *
     * @param transaction The transaction to be created.
     * @return The saved transaction.
     */
    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Processes a transaction by creating it and saving it to the repository.
     *
     * @param wallet The wallet associated with the transaction.
     * @param request The payment request containing transaction details.
     * @param transactionStatus The status of the transaction.
     * @param transactionType The type of the transaction (e.g., DEPOSIT, WITHDRAW).
     * @return The created transaction.
     */
    @Override
    public Transaction processTransaction(Wallet wallet, PaymentRequest request, TransactionStatus transactionStatus, TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(transactionType);
        transaction.setAmount(request.getAmount());
        transaction.setOppositePartyType(request.getSource());
        transaction.setOppositeParty(request.getOppositeParty());
        transaction.setStatus(transactionStatus);

        return createTransaction(transaction);
    }

    /**
     * Finds all transactions associated with a specific wallet ID.
     *
     * @param walletId The ID of the wallet.
     * @return A list of transaction responses mapped from the transactions.
     */
    @Override
    public List<TransactionResponse> findTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId).stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();
    }

    /**
     * Finds a specific transaction by wallet ID and transaction ID.
     *
     * @param walletId The ID of the wallet.
     * @param transactionId The ID of the transaction.
     * @return The transaction response mapped from the transaction.
     */
    public TransactionResponse findTransaction(Long walletId, Long transactionId) {
        Transaction transaction = transactionRepository.findByWalletIdAndId(walletId,transactionId);
        return transactionMapper.toTransactionResponse(transaction);
    }

    /**
     * Approves a transaction and updates the wallet balance accordingly.
     *
     * @param request The transaction approval request containing the transaction ID and status.
     * @return The updated transaction response.
     * @throws WalletNotFoundException If the transaction is not found.
     */
    @Override
    public TransactionResponse approveTransaction(TransactionApprovalRequest request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new WalletNotFoundException("Transaction not found"));

        Wallet wallet = transaction.getWallet();

        if (request.getStatus() == TransactionStatus.APPROVED) {
            if (transaction.getType() == TransactionType.DEPOSIT) {
                wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
            } else if (transaction.getType() == TransactionType.WITHDRAW) {
                wallet.setUsableBalance(wallet.getUsableBalance().subtract(transaction.getAmount()));
            }
        }

        transaction.setStatus(request.getStatus());
        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        return findTransaction(wallet.getId(), transaction.getId());
    }

}