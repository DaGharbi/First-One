package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SOLDE_CAISSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldeCaisse {
    @EmbeddedId
    private SoldeCaisseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "SOLDE_CAISSE", precision = 15, scale = 3)
    private BigDecimal soldeCaisse;
}
