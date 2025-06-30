package com.inghubs.walletservice.service;

import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import com.inghubs.walletservice.model.entity.Transaction;
import com.inghubs.walletservice.model.entity.Wallet;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    Transaction processTransaction(Wallet wallet, PaymentRequest request, TransactionStatus transactionStatus, TransactionType transactionType);

    List<TransactionResponse> findTransactionsByWalletId(Long walletId);

    TransactionResponse approveTransaction(TransactionApprovalRequest request);

}