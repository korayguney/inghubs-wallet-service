package com.inghubs.walletservice.model.dto;

import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionApprovalRequest {

    @NotNull(message = "Transaction ID is required")
    private Long transactionId;

    @NotNull(message = "Transaction status is required")
    private TransactionStatus status;
}