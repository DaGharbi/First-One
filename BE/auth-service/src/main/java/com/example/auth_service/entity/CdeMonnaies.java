package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "CDE_MONNAIES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CdeMonnaies {
    @EmbeddedId
    private CdeMonnaiesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "P_1_DN", nullable = false)
    private Long p1Dn;
    @Column(name = "P_500_M", nullable = false)
    private Long p500M;
    @Column(name = "P_100_M", nullable = false)
    private Long p100M;
    @Column(name = "P_50_M", nullable = false)
    private Long p50M;
    @Column(name = "P_20_M", nullable = false)
    private Long p20M;
    @Column(name = "P_10_M", nullable = false)
    private Long p10M;
    @Column(name = "P_5_M", nullable = false)
    private Long p5M;
}
