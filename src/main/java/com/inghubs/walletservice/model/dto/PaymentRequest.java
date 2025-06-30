package com.inghubs.walletservice.model.dto;

    import com.inghubs.walletservice.model.dto.enums.OppositePartyType;
    import jakarta.validation.constraints.NotNull;
    import lombok.Getter;
    import lombok.Setter;

    import java.math.BigDecimal;

    @Getter
    @Setter
    public class PaymentRequest {

        @NotNull(message = "Wallet ID is required")
        private Long walletId;

        @NotNull(message = "Amount is required")
        private BigDecimal amount;

        @NotNull(message = "Source is required and must be either IBAN or PAYMENT")
        private OppositePartyType source;

        @NotNull(message = "Opposite party is required")
        private String oppositeParty;
    }