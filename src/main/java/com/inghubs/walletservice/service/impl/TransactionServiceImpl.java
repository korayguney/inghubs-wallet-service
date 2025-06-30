package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.WalletTransactionException;
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

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

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

    @Override
    public List<TransactionResponse> findTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId).stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();
    }

    public TransactionResponse findTransaction(Long walletId, Long transactionId) {
        Transaction transaction = transactionRepository.findByWalletIdAndId(walletId,transactionId);
        return transactionMapper.toTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse approveTransaction(TransactionApprovalRequest request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new WalletTransactionException("Transaction not found"));

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