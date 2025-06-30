package com.inghubs.walletservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private Long walletId;
    private String currency;
    private BigDecimal totalBalance;
    private BigDecimal usableBalance;
    private String transactionStatus;
}