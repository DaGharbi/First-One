package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "OPERATION_CAISSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationCaisse {
    @EmbeddedId
    private OperationCaisseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "LIB_OP", length = 100)
    private String libOp;

    @Column(name = "MONTANT", precision = 15, scale = 3)
    private BigDecimal montant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE")
    private Devise devise;
}
