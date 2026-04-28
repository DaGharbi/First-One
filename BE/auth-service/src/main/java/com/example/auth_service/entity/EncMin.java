package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ENC_MIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncMin {
    @EmbeddedId
    private EncMinId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "ENC_MIN", precision = 15, scale = 3)
    private BigDecimal encMin;
    @Column(name = "TOT_RET", precision = 15, scale = 3)
    private BigDecimal totRet;
    @Column(name = "TOT_VERS", precision = 15, scale = 3)
    private BigDecimal totVers;
    @Column(name = "GAP", precision = 15, scale = 3)
    private BigDecimal gap;
    @Column(name = "ENC_OPT", precision = 15, scale = 3)
    private BigDecimal encOpt;
}
