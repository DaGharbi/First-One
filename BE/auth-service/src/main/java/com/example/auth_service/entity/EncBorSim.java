package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ENC_BOR_SIM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncBorSim {
    @EmbeddedId
    private EncBorId id; // Uses the same ID structure as EncBor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "ENC_MIN", precision = 15, scale = 3)
    private BigDecimal encMin;
    @Column(name = "ENC_MAX", precision = 15, scale = 3)
    private BigDecimal encMax;
    @Column(name = "ENC_OPT", precision = 15, scale = 3)
    private BigDecimal encOpt;
}
