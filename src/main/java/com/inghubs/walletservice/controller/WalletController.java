package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
