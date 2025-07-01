package com.inghubs.walletservice.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private Long walletId;
    private String currency;
    private BigDecimal totalBalance;
    private BigDecimal usableBalance;
    private String transactionStatus;
}