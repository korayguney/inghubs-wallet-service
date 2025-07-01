package com.inghubs.walletservice.model.dto;

import com.inghubs.walletservice.model.dto.enums.OppositePartyType;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private TransactionType type;
    private OppositePartyType oppositePartyType;
    private String oppositeParty;
    private TransactionStatus status;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
}
