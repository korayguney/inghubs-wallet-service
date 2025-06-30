package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactionsByWallet(@RequestParam Long walletId) {
        List<TransactionResponse> responseList = transactionService.findTransactionsByWalletId(walletId);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/approve")
    public ResponseEntity<TransactionResponse> approveTransaction(@RequestBody TransactionApprovalRequest request) {
        TransactionResponse response = transactionService.approveTransaction(request);
        return ResponseEntity.ok(response);
    }
}