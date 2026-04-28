package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "GRPLIMITE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Grplimite implements Serializable {

    @Id
    @Column(name = "ID_GROUPE", unique = true, nullable = false, precision = 22, scale = 0)
    private BigDecimal idGroupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", nullable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", nullable = false)
    private Agence agence;

    @Column(name = "GROUPE", nullable = false, length = 10)
    private String groupe;
}
