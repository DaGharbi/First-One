package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "TRF_FND_IBS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrfFndIbs implements Serializable {
    @EmbeddedId
    private TrfFndIbsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "MONTANT_PROPOSE", precision = 15, scale = 3)
    private BigDecimal montantPropose;

    @Column(name = "MONTANT_MODIFIE", precision = 15, scale = 3)
    private BigDecimal montantModifie;

    @Column(name = "ETAT", length = 30)
    private String etat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIF")
    private Date dateModif;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_VALID")
    private Date dateValid;
}
