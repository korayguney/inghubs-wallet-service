package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.WalletNotFoundException;
import com.inghubs.walletservice.mapper.TransactionMapper;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.model.dto.enums.OppositePartyType;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import com.inghubs.walletservice.model.entity.Transaction;
import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.TransactionRepository;
import com.inghubs.walletservice.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final TransactionMapper transactionMapper = mock(TransactionMapper.class);
    private final TransactionServiceImpl transactionService = new TransactionServiceImpl(transactionRepository, walletRepository, transactionMapper);

    @Test
    @DisplayName("createTransaction saves and returns the transaction")
    void createTransactionSavesAndReturnsTransaction() {
        // given
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // when
        Transaction result = transactionService.createTransaction(transaction);

        // then
        assertEquals(transaction, result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("processTransaction creates and saves transaction")
    void processTransactionCreatesAndSavesTransaction() {
        // given
        Wallet wallet = new Wallet();
        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .source(OppositePartyType.IBAN)
                .oppositeParty("OPPOSITE_PARTY")
                .build();
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // when
        Transaction result = transactionService.processTransaction(wallet, request, TransactionStatus.PENDING, TransactionType.DEPOSIT);

        // then
        assertEquals(wallet, result.getWallet());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("findTransactionsByWalletId returns mapped responses")
    void findTransactionsByWalletIdReturnsMappedResponses() {
        // given
        Long walletId = 1L;
        Transaction transaction = new Transaction();
        TransactionResponse response = new TransactionResponse();
        when(transactionRepository.findByWalletId(walletId)).thenReturn(List.of(transaction));
        when(transactionMapper.toTransactionResponse(transaction)).thenReturn(response);

        // when
        List<TransactionResponse> responses = transactionService.findTransactionsByWalletId(walletId);

        // then
        assertEquals(1, responses.size());
        assertEquals(response, responses.get(0));
        verify(transactionRepository, times(1)).findByWalletId(walletId);
        verify(transactionMapper, times(1)).toTransactionResponse(transaction);
    }

    @Test
    @DisplayName("findTransaction returns mapped response")
    void findTransactionReturnsMappedResponse() {
        // given
        Long walletId = 1L;
        Long transactionId = 2L;
        Transaction transaction = new Transaction();
        TransactionResponse response = new TransactionResponse();
        when(transactionRepository.findByWalletIdAndId(walletId, transactionId)).thenReturn(transaction);
        when(transactionMapper.toTransactionResponse(transaction)).thenReturn(response);

        // when
        TransactionResponse result = transactionService.findTransaction(walletId, transactionId);

        // then
        assertEquals(response, result);
        verify(transactionRepository, times(1)).findByWalletIdAndId(walletId, transactionId);
        verify(transactionMapper, times(1)).toTransactionResponse(transaction);
    }

    @Test
    @DisplayName("approveTransaction updates wallet balance for deposit")
    void approveTransactionUpdatesWalletBalanceForDeposit() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        Wallet wallet = new Wallet();
        wallet.setUsableBalance(BigDecimal.valueOf(200));
        transaction.setWallet(wallet);
        TransactionApprovalRequest request = new TransactionApprovalRequest();
        request.setTransactionId(1L);
        request.setStatus(TransactionStatus.APPROVED);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // when
        TransactionResponse response = transactionService.approveTransaction(request);

        // then
        assertEquals(BigDecimal.valueOf(300), wallet.getUsableBalance());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("approveTransaction throws exception if transaction not found")
    void approveTransactionThrowsExceptionIfTransactionNotFound() {
        // given
        TransactionApprovalRequest request = new TransactionApprovalRequest();
        request.setTransactionId(1L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> transactionService.approveTransaction(request));

        // then
        assertEquals("Transaction not found", exception.getMessage());
        verify(transactionRepository, times(1)).findById(1L);
    }
}