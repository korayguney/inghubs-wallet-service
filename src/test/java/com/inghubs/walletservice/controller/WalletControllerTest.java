package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

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
                .currency(null) // Assuming "INVALID" is not a valid enum value
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
}