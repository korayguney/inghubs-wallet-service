package com.inghubs.walletservice.controller;

import com.inghubs.walletservice.model.dto.TransactionApprovalRequest;
import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling transaction-related requests.
 * Provides endpoints for listing transactions by wallet and approving transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Retrieves a list of transactions associated with a specific wallet.
     *
     * @param walletId The ID of the wallet whose transactions are to be retrieved.
     * @return A ResponseEntity containing a list of TransactionResponse objects.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role or is a 'CUSTOMER'
     *               who owns the specified wallet.
     */
    @GetMapping
    @PreAuthorize("""
                hasRole('EMPLOYEE') or 
                (hasRole('CUSTOMER') and @walletServiceImpl.isWalletOwnedBy(#walletId, principal.id))
            """)
    public ResponseEntity<List<TransactionResponse>> listTransactionsByWallet(@RequestParam Long walletId) {
        List<TransactionResponse> responseList = transactionService.findTransactionsByWalletId(walletId);
        return ResponseEntity.ok(responseList);
    }

    /**
     * Approves a transaction based on the provided request data.
     *
     * @param request The TransactionApprovalRequest object containing transaction details and approval status.
     * @return A ResponseEntity containing the updated TransactionResponse object.
     * @PreAuthorize Ensures that the user has the 'EMPLOYEE' role to approve transactions.
     */
    @PostMapping("/approve")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TransactionResponse> approveTransaction(@RequestBody TransactionApprovalRequest request) {
        TransactionResponse response = transactionService.approveTransaction(request);
        return ResponseEntity.ok(response);
    }
}