package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.mapper.WalletMapper;
import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.model.entity.Customer;
import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.WalletRepository;
import com.inghubs.walletservice.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class WalletServiceImplTest {

    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final TransactionService transactionService = mock(TransactionService.class);
    private final WalletMapper walletMapper = mock(WalletMapper.class);
    private final WalletServiceImpl walletService = new WalletServiceImpl(walletRepository, customerRepository, transactionService, walletMapper);

    @Test
    @DisplayName("createWallet successfully creates wallet for valid customer")
    void createWalletSuccessfullyCreatesWalletForValidCustomer() {
        CreateWalletRequest request = CreateWalletRequest.builder()
                .customerId(1L)
                .walletName("My Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();
        Customer customer = new Customer();
        Wallet wallet = new Wallet();
        Wallet savedWallet = new Wallet();
        CreateWalletResponse expectedResponse = CreateWalletResponse.builder()
                .walletId(1L)
                .walletName("My Wallet")
                .currency("USD")
                .balance(BigDecimal.valueOf(0.0))
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(walletMapper.toEntity(request)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(savedWallet);
        when(walletMapper.toResponse(savedWallet)).thenReturn(expectedResponse);

        CreateWalletResponse response = walletService.createWallet(request);

        assertEquals(expectedResponse, response);
        verify(customerRepository, times(1)).findById(1L);
        verify(walletMapper, times(1)).toEntity(request);
        verify(walletRepository, times(1)).save(wallet);
        verify(walletMapper, times(1)).toResponse(savedWallet);
    }

    @Test
    @DisplayName("createWallet throws exception when customer does not exist")
    void createWalletThrowsExceptionWhenCustomerDoesNotExist() {
        CreateWalletRequest request = CreateWalletRequest.builder()
                .customerId(99L)
                .walletName("My Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> walletService.createWallet(request));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(99L);
        verify(walletMapper, never()).toEntity(any());
        verify(walletRepository, never()).save(any());
        verify(walletMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("createWallet sets initial balance to zero regardless of request")
    void createWalletSetsInitialBalanceToZeroRegardlessOfRequest() {
        CreateWalletRequest request = CreateWalletRequest.builder()
                .customerId(1L)
                .walletName("My Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();
        Customer customer = new Customer();
        Wallet wallet = new Wallet();
        Wallet savedWallet = new Wallet();
        savedWallet.setBalance(BigDecimal.ZERO);
        savedWallet.setUsableBalance(BigDecimal.ZERO);
        CreateWalletResponse expectedResponse = CreateWalletResponse.builder()
                .walletId(1L)
                .walletName("My Wallet")
                .currency("USD")
                .balance(BigDecimal.ZERO)
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(walletMapper.toEntity(request)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(savedWallet);
        when(walletMapper.toResponse(savedWallet)).thenReturn(expectedResponse);

        CreateWalletResponse response = walletService.createWallet(request);

        assertEquals(BigDecimal.ZERO, savedWallet.getBalance());
        assertEquals(BigDecimal.ZERO, savedWallet.getUsableBalance());
        assertEquals(expectedResponse, response);
        verify(customerRepository, times(1)).findById(1L);
        verify(walletMapper, times(1)).toEntity(request);
        verify(walletRepository, times(1)).save(wallet);
        verify(walletMapper, times(1)).toResponse(savedWallet);
    }

}
