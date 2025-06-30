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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody @Valid CreateWalletRequest request) {
        CreateWalletResponse response = walletService.createWallet(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CreateWalletResponse>> listWallets(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Currency currency,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {
        List<CreateWalletResponse> wallets = walletService.listWallets(customerId, currency, minAmount, maxAmount);
        return ResponseEntity.ok(wallets);
    }

    @PostMapping("/deposit")
    public ResponseEntity<PaymentResponse> makeDeposit(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = walletService.makeDeposit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<PaymentResponse> makeWithdraw(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = walletService.makeWithdraw(request);
        return ResponseEntity.ok(response);
    }
}
