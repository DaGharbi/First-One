package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@Entity
@Table(name = "CONVOYEURS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Convoyeurs {
    @Id
    @Column(name = "CIN", nullable = false, length = 8)
    private String cin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_PREST", nullable = false)
    private Prestataire prestataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CENT")
    private CentreFort centreFort;

    @Column(name = "NOM", nullable = false, length = 30)
    private String nom;

    @Column(name = "PRENOM", nullable = false, length = 30)
    private String prenom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATNAISS", nullable = false)
    private Date datnaiss;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FONCTION", nullable = false)
    private Date dateFonction;

    @Column(name = "STATUT", nullable = false, length = 1)
    private String statut;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_EFFET", nullable = false)
    private Date dateEffet;

    /**
     * In Oracle 10g legacy environments this column is often LONG RAW and can
     * cause JDBC stream errors during pagination reads.
     *
     * We store/retrieve images using `IMAGE_CP_CONVOYEURS` (`ConvoyeurImage`)
     * instead, so we intentionally do not map `IMAGE_CIN` here.
     */
    @Transient
    private byte[] imageCin;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATOPER")
    private Date datoper;

    @Column(name = "UTILISATEUR", length = 6)
    private String utilisateur;
}
