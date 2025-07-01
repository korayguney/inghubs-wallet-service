package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.PaymentResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WalletControllerTest {

    private final WalletService walletService = mock(WalletService.class);
    private final WalletController walletController = new WalletController(walletService);

    @Test
    @DisplayName("createWallet returns response for valid request")
    void createWalletReturnsResponseForValidRequest() {
        // given
        CreateWalletRequest request = CreateWalletRequest.builder()
                .customerId(1L)
                .walletName("My Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();
        CreateWalletResponse expectedResponse = CreateWalletResponse.builder()
                .walletId(1L)
                .walletName("My Wallet")
                .currency("USD")
                .balance(BigDecimal.valueOf(0.0))
                .build();
        when(walletService.createWallet(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<CreateWalletResponse> response = walletController.createWallet(request);

        // then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(walletService, times(1)).createWallet(request);
    }

    @Test
    @DisplayName("createWallet throws exception for invalid request")
    void createWalletThrowsExceptionForInvalidRequest() {
        // given
        CreateWalletRequest invalidRequest = CreateWalletRequest.builder()
                .customerId(null)
                .walletName("")
                .currency(null)
                .activeForShopping(false)
                .activeForWithdraw(false)
                .build();
        when(walletService.createWallet(invalidRequest)).thenThrow(new IllegalArgumentException("Invalid wallet data"));

        // when & then
        try {
            walletController.createWallet(invalidRequest);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid wallet data", e.getMessage());
        }

        verify(walletService, times(1)).createWallet(invalidRequest);
    }

    @Test
    @DisplayName("listWallets returns wallets filtered by customerId")
    void listWalletsReturnsWalletsFilteredByCustomerId() {
        // given
        Long customerId = 1L;
        List<CreateWalletResponse> expectedWallets = List.of(
                CreateWalletResponse.builder()
                        .walletId(1L)
                        .walletName("Wallet 1")
                        .currency("USD")
                        .balance(BigDecimal.valueOf(100.0))
                        .build()
        );
        when(walletService.listWallets(customerId, null, null, null)).thenReturn(expectedWallets);

        // when
        ResponseEntity<List<CreateWalletResponse>> response = walletController.listWallets(customerId, null, null, null);

        // then
        assertEquals(ResponseEntity.ok(expectedWallets), response);
        verify(walletService, times(1)).listWallets(customerId, null, null, null);
    }

    @Test
    @DisplayName("listWallets returns empty list when no wallets match filters")
    void listWalletsReturnsEmptyListWhenNoWalletsMatchFilters() {
        // given
        Long customerId = 99L;
        when(walletService.listWallets(customerId, null, null, null)).thenReturn(List.of());

        // when
        ResponseEntity<List<CreateWalletResponse>> response = walletController.listWallets(customerId, null, null, null);

        // then
        assertEquals(ResponseEntity.ok(List.of()), response);
        verify(walletService, times(1)).listWallets(customerId, null, null, null);
    }

    @Test
    @DisplayName("makeDeposit processes deposit for valid request")
    void makeDepositProcessesDepositForValidRequest() {
        // given
        PaymentRequest request = PaymentRequest.builder()
                .walletId(1L)
                .amount(BigDecimal.valueOf(50.0))
                .build();
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .walletId(1L)
                .totalBalance(BigDecimal.valueOf(50.0))
                .transactionStatus("SUCCESS")
                .build();
        when(walletService.makeDeposit(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<PaymentResponse> response = walletController.makeDeposit(request);

        // then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(walletService, times(1)).makeDeposit(request);
    }

    @Test
    @DisplayName("makeWithdraw processes withdrawal for valid request")
    void makeWithdrawProcessesWithdrawalForValidRequest() {
        // given
        PaymentRequest request = PaymentRequest.builder()
                .walletId(1L)
                .amount(BigDecimal.valueOf(30.0))
                .build();
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .walletId(1L)
                .totalBalance(BigDecimal.valueOf(30.0))
                .transactionStatus("SUCCESS")
                .build();
        when(walletService.makeWithdraw(request)).thenReturn(expectedResponse);

        // when
        ResponseEntity<PaymentResponse> response = walletController.makeWithdraw(request);

        // then
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(walletService, times(1)).makeWithdraw(request);
    }

    @Test
    @DisplayName("makeWithdraw throws exception for insufficient balance")
    void makeWithdrawThrowsExceptionForInsufficientBalance() {
        // given
        PaymentRequest request = PaymentRequest.builder()
                .walletId(1L)
                .amount(BigDecimal.valueOf(1000.0))
                .build();
        when(walletService.makeWithdraw(request)).thenThrow(new IllegalArgumentException("Insufficient balance"));

        // when & then
        try {
            walletController.makeWithdraw(request);
        } catch (IllegalArgumentException e) {
            assertEquals("Insufficient balance", e.getMessage());
        }

        verify(walletService, times(1)).makeWithdraw(request);
    }
}