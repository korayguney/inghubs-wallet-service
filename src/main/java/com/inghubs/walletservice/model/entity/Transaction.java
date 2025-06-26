package com.inghubs.walletservice.model.entity;

import com.inghubs.walletservice.model.dto.enums.OppositePartyType;
import com.inghubs.walletservice.model.dto.enums.TransactionStatus;
import com.inghubs.walletservice.model.dto.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OppositePartyType oppositePartyType;

    @Column(nullable = false)
    private String oppositeParty; // e.g. IBAN or Payment reference

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
}