package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.dto.PaymentRequest;
import com.inghubs.walletservice.model.dto.PaymentResponse;
import com.inghubs.walletservice.model.dto.enums.Currency;
import com.inghubs.walletservice.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for handling wallet-related operations.
 * Provides endpoints for creating wallets, listing wallets, and processing deposits and withdrawals.
 */
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Creates a new wallet for a customer.
     *
     * @param request The request object containing wallet details such as customer ID and wallet name.
     * @return A ResponseEntity containing the created wallet details.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role or is a 'CUSTOMER' who owns the wallet.
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or (hasRole('CUSTOMER') and #request.customerId == principal.id)")
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody @Valid CreateWalletRequest request) {
        CreateWalletResponse response = walletService.createWallet(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists wallets based on optional filters such as customer ID, currency, and balance range.
     *
     * @param customerId The ID of the customer whose wallets are to be listed (optional).
     * @param currency   The currency type to filter wallets (optional).
     * @param minAmount  The minimum balance to filter wallets (optional).
     * @param maxAmount  The maximum balance to filter wallets (optional).
     * @return A ResponseEntity containing a list of wallets matching the filters.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role or is a 'CUSTOMER' who owns the wallets.
     */
    @GetMapping
    @PreAuthorize("""
                hasRole('EMPLOYEE') or
                (hasRole('CUSTOMER') and #customerId != null and #customerId == principal.id)
            """)
    public ResponseEntity<List<CreateWalletResponse>> listWallets(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Currency currency,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {
        List<CreateWalletResponse> wallets = walletService.listWallets(customerId, currency, minAmount, maxAmount);
        return ResponseEntity.ok(wallets);
    }

    /**
     * Processes a deposit into a wallet.
     *
     * @param request The request object containing wallet ID and deposit amount.
     * @return A ResponseEntity containing the details of the deposit transaction.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role or is a 'CUSTOMER' who owns the wallet.
     */
    @PostMapping("/deposit")
    @PreAuthorize("""
                hasRole('EMPLOYEE') or 
                (hasRole('CUSTOMER') and @walletRepository.findById(#request.walletId).get().customer.id == principal.id)
            """)
    public ResponseEntity<PaymentResponse> makeDeposit(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = walletService.makeDeposit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Processes a withdrawal from a wallet.
     *
     * @param request The request object containing wallet ID and withdrawal amount.
     * @return A ResponseEntity containing the details of the withdrawal transaction.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role or is a 'CUSTOMER' who owns the wallet.
     */
    @PostMapping("/withdraw")
    @PreAuthorize("""
                hasRole('EMPLOYEE') or 
                (hasRole('CUSTOMER') and @walletRepository.findById(#request.walletId).get().customer.id == principal.id)
            """)
    public ResponseEntity<PaymentResponse> makeWithdraw(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = walletService.makeWithdraw(request);
        return ResponseEntity.ok(response);
    }
}
