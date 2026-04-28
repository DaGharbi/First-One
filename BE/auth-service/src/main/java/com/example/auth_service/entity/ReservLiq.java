package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "RESERV_LIQ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservLiq {
    @EmbeddedId
    private ReservLiqId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USR_MAT", nullable = false)
    private Users user;

    @Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
    @Column(name = "DATE_SAI", nullable = false)
    private java.util.Date dateSai;

    @Column(name = "MNT_RES", nullable = false, precision = 15, scale = 3)
    private BigDecimal mntRes;

    @Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIF")
    private java.util.Date dateModif;
}
