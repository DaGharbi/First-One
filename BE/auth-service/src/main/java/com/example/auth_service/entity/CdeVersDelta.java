package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CDE_VERS_DELTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CdeVersDelta {
    @EmbeddedId
    private CdeVersDeltaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "MONTANT", precision = 15, scale = 3)
    private BigDecimal montant;

    @Column(name = "CDE_VERS", nullable = false, precision = 1, scale = 0)
    private Short cdeVers;

    @Column(name = "DESCRIPTION_CLIENT", length = 500)
    private String descriptionClient;

    @Column(name = "IS_BILLETS", precision = 1, scale = 0)
    private Boolean isBillets;
}
