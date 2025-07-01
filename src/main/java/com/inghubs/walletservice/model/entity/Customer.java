package com.inghubs.walletservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    @Column(unique = true, nullable = false, length = 11)
    private String tckn;
}
