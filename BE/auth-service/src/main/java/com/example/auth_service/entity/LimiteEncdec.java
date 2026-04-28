package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Entity
@Table(name = "LIMITE_ENCDEC")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LimiteEncdec {
    @Id
    @Column(name = "ID_LIMITE", unique = true, nullable = false, precision = 22, scale = 0)
    private BigDecimal idLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", nullable = false)
    private Devise devise;

    @Column(name = "GROUPE", nullable = false, length = 10)
    private String groupe;

    @jakarta.persistence.Temporal(jakarta.persistence.TemporalType.DATE)
    @Column(name = "DATE_ENC", nullable = false)
    private java.util.Date dateEnc;

    @Column(name = "ENC_MIN", precision = 15, scale = 3)
    private BigDecimal encMin;

    @Column(name = "ENC_MAX", precision = 15, scale = 3)
    private BigDecimal encMax;

    @Column(name = "ENC_OPT", precision = 15, scale = 3)
    private BigDecimal encOpt;
}
