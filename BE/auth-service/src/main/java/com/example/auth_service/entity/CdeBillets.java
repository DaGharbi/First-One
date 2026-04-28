package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CDE_BILLETS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CdeBillets {
    @EmbeddedId
    private CdeBilletsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "B_50_DN")
    private Long b50Dn;
    @Column(name = "B_30_DN")
    private Long b30Dn;
    @Column(name = "B_20_DN")
    private Long b20Dn;
    @Column(name = "B_10_DN")
    private Long b10Dn;
    @Column(name = "B_5_DN")
    private Long b5Dn;
    @Column(name = "B_20_EUR")
    private Long b20Eur;
    @Column(name = "B_50_EUR")
    private Long b50Eur;
    @Column(name = "B_100_EUR")
    private Long b100Eur;
    @Column(name = "B_200_EUR")
    private Long b200Eur;
    @Column(name = "B_500_EUR")
    private Long b500Eur;
    @Column(name = "B_10_USD")
    private Long b10Usd;
    @Column(name = "B_20_USD")
    private Long b20Usd;
    @Column(name = "B_50_USD")
    private Long b50Usd;
    @Column(name = "B_100_USD")
    private Long b100Usd;
}
