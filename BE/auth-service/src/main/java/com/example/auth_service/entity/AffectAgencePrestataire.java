package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@Entity
@Table(name = "AFFECT_AGENCE_PRESTATAIRE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE_AGENCE",
        "CODE_PREST" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AffectAgencePrestataire {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_affect_agence_prestataire")
    @SequenceGenerator(name = "seq_affect_agence_prestataire", sequenceName = "SEQ_AFFECT_AGENCE_PRESTATAIRE", allocationSize = 1)
    @Column(name = "ID_AFFECT")
    private Integer idAffect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_PREST", nullable = false)
    private Prestataire prestataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", nullable = false)
    private Agence agence;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_AFFECTATION", nullable = false)
    private Date dateAffectation;
}
