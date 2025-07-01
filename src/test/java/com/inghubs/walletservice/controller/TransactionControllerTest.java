package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    private final TransactionService transactionService = mock(TransactionService.class);
    private final TransactionController transactionController = new TransactionController(transactionService);

    @Test
    @DisplayName("listTransactionsByWallet returns transactions for valid walletId")
    void listTransactionsByWalletReturnsTransactionsForValidWalletId() {
        // given
        Long walletId = 1L;
        List<TransactionResponse> expectedTransactions = List.of(
                TransactionResponse.builder()
                        .walletId(walletId)
                        .status(TransactionStatus.APPROVED)
                        .build()
        );
        when(transactionService.findTransactionsByWalletId(walletId)).thenReturn(expectedTransactions);

        // when
        ResponseEntity<List<TransactionResponse>> response = transactionController.listTransactionsByWallet(walletId);

        // then
        assertEquals(ResponseEntity.ok(expectedTransactions), response);
        verify(transactionService, times(1)).findTransactionsByWalletId(walletId);
    }

    @Test
    @DisplayName("listTransactionsByWallet returns empty list for invalid walletId")
    void listTransactionsByWalletReturnsEmptyListForInvalidWalletId() {
        // given
        Long walletId = 99L;
        when(transactionService.findTransactionsByWalletId(walletId)).thenReturn(List.of());

        // when
        ResponseEntity<List<TransactionResponse>> response = transactionController.listTransactionsByWallet(walletId);

        // then
        assertEquals(ResponseEntity.ok(List.of()), response);
        verify(transactionService, times(1)).findTransactionsByWalletId(walletId);
    }

    @Test
    @DisplayName("approveTransaction updates transaction status for valid request")
    void approveTransactionUpdatesTransactionStatusForValidRequest() {
        // given
        TransactionApprovalRequest request = TransactionApprovalRequest.builder()
                .transactionId(1L)
                .status(TransactionStatus.APPROVED)
                .build();
        TransactionResponse expectedResponse = TransactionResponse.builder()
                .walletId(1L)
                .status(TransactionStatus.APPROVED)
                .build();
        when(transactionService.approveTransaction(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<TransactionResponse> response = transactionController.approveTransaction(request);

        // then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(transactionService, times(1)).approveTransaction(request);
    }

    @Test
    @DisplayName("approveTransaction throws exception for invalid request")
    void approveTransactionThrowsExceptionForInvalidRequest() {
        // given
        TransactionApprovalRequest invalidRequest = TransactionApprovalRequest.builder()
                .transactionId(null)
                .status(null)
                .build();
        when(transactionService.approveTransaction(invalidRequest)).thenThrow(new IllegalArgumentException("Invalid transaction data"));

        // when & then
        try {
            transactionController.approveTransaction(invalidRequest);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid transaction data", e.getMessage());
        }

        verify(transactionService, times(1)).approveTransaction(invalidRequest);
    }
}