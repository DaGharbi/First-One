package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncMinId implements Serializable {

    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;

    @Column(name = "CODE_DEVISE", length = 3)
    private String codeDevise;

    /** Legacy key stored as formatted day (e.g. dd/MM/yyyy) matching CalcEncaisseMin / Oracle TO_CHAR. */
    @Column(name = "DATE_ENC_min", length = 10, nullable = false)
    private String dateEncMin;
}
