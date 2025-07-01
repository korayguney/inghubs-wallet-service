package com.inghubs.walletservice.model.dto;

import com.inghubs.walletservice.model.dto.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateWalletRequest {

    @NotBlank(message = "Wallet name is required")
    private String walletName;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "activeForShopping is required")
    private Boolean activeForShopping;

    @NotNull(message = "activeForWithdraw is required")
    private Boolean activeForWithdraw;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
}
