package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.exception.CustomerNotFoundException;
import com.inghubs.walletservice.exception.WithdrawNotAllowedException;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
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
        // given
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

        // when
        CreateWalletResponse response = walletService.createWallet(request);

        // then
        assertEquals(expectedResponse, response);
        verify(customerRepository, times(1)).findById(1L);
        verify(walletMapper, times(1)).toEntity(request);
        verify(walletRepository, times(1)).save(wallet);
        verify(walletMapper, times(1)).toResponse(savedWallet);
    }

    @Test
    @DisplayName("createWallet throws exception when customer does not exist")
    void createWalletThrowsExceptionWhenCustomerDoesNotExist() {
        // given
        CreateWalletRequest request = CreateWalletRequest.builder()
                .customerId(99L)
                .walletName("My Wallet")
                .currency(Currency.EUR)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> walletService.createWallet(request));

        // then
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(99L);
        verify(walletMapper, never()).toEntity(any());
        verify(walletRepository, never()).save(any());
        verify(walletMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("createWallet sets initial balance to zero regardless of request")
    void createWalletSetsInitialBalanceToZeroRegardlessOfRequest() {
        // given
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

        // when
        CreateWalletResponse response = walletService.createWallet(request);

        // then
        assertEquals(BigDecimal.ZERO, savedWallet.getBalance());
        assertEquals(BigDecimal.ZERO, savedWallet.getUsableBalance());
        assertEquals(expectedResponse, response);
        verify(customerRepository, times(1)).findById(1L);
        verify(walletMapper, times(1)).toEntity(request);
        verify(walletRepository, times(1)).save(wallet);
        verify(walletMapper, times(1)).toResponse(savedWallet);
    }

    @Test
    @DisplayName("listWallets returns filtered wallets")
    void listWalletsReturnsFilteredWallets() {
        // given
        Long customerId = 1L;
        Currency currency = Currency.EUR;
        BigDecimal minAmount = BigDecimal.valueOf(100);
        BigDecimal maxAmount = BigDecimal.valueOf(1000);
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.valueOf(500));
        List<Wallet> wallets = List.of(wallet);
        when(walletRepository.findWalletsByFilters(customerId, currency, minAmount, maxAmount)).thenReturn(wallets);
        when(walletMapper.toResponse(wallet)).thenReturn(CreateWalletResponse.builder()
                .walletId(wallet.getId())
                .currency(wallet.getCurrency().name())
                .balance(wallet.getBalance())
                .build());

        // when
        List<CreateWalletResponse> responses = walletService.listWallets(customerId, currency, minAmount, maxAmount);

        // then
        assertEquals(1, responses.size());
        assertEquals("EUR", responses.get(0).getCurrency());
        verify(walletRepository, times(1)).findWalletsByFilters(customerId, currency, minAmount, maxAmount);
        verify(walletMapper, times(1)).toResponse(wallet);
    }

    @Test
    @DisplayName("makeDeposit successfully processes deposit")
    void makeDepositSuccessfullyProcessesDeposit() {
        // given
        PaymentRequest request = PaymentRequest.builder()
                .walletId(1L)
                .amount(BigDecimal.valueOf(500))
                .build();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(1000));
        wallet.setUsableBalance(BigDecimal.valueOf(1000));
        wallet.setCurrency(Currency.EUR);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionService.processTransaction(wallet, request, TransactionStatus.APPROVED, TransactionType.DEPOSIT))
                .thenReturn(null);

        // when
        PaymentResponse response = walletService.makeDeposit(request);

        // then
        assertEquals(BigDecimal.valueOf(1500), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(1500), wallet.getUsableBalance());
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionService, times(1)).processTransaction(wallet, request, TransactionStatus.APPROVED, TransactionType.DEPOSIT);
    }

    @Test
    @DisplayName("makeWithdraw throws exception if wallet is inactive for withdraw")
    void makeWithdrawThrowsExceptionIfWalletIsInactiveForWithdraw() {
        // given
        PaymentRequest request = PaymentRequest.builder()
                .walletId(1L)
                .amount(BigDecimal.valueOf(500))
                .build();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setActiveForWithdraw(false);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        // when
        WithdrawNotAllowedException exception = assertThrows(WithdrawNotAllowedException.class, () -> walletService.makeWithdraw(request));

        // then
        assertEquals("Withdraw is not allowed for this wallet", exception.getMessage());
        verify(walletRepository, times(1)).findById(1L);
        verify(transactionService, never()).processTransaction(any(), any(), any(), any());
    }

    @Test
    @DisplayName("isWalletOwnedBy returns true if wallet belongs to customer")
    void isWalletOwnedByReturnsTrueIfWalletBelongsToCustomer() {
        // given
        Long walletId = 1L;
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setCustomer(customer);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // when
        boolean result = walletService.isWalletOwnedBy(walletId, customerId);

        // then
        assertTrue(result);
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    @DisplayName("isWalletOwnedBy returns false if wallet does not belong to customer")
    void isWalletOwnedByReturnsFalseIfWalletDoesNotBelongToCustomer() {
        // given
        Long walletId = 1L;
        Long customerId = 2L;
        Customer customer = new Customer();
        customer.setId(1L);
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setCustomer(customer);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // when
        boolean result = walletService.isWalletOwnedBy(walletId, customerId);

        // then
        assertFalse(result);
        verify(walletRepository, times(1)).findById(walletId);
    }

}
