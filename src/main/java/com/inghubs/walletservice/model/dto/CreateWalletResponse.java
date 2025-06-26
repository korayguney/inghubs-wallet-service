package com.inghubs.walletservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateWalletResponse {

    private Long walletId;
    private String walletName;
    private String currency;
    private Boolean activeForShopping;
    private Boolean activeForWithdraw;
    private BigDecimal balance;
    private BigDecimal usableBalance;
    private Long customerId;
}