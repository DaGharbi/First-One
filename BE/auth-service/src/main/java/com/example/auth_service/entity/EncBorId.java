package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncBorId implements Serializable {
    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;

    @Column(name = "CODE_DEVISE", length = 3)
    private String codeDevise;

    @jakarta.persistence.Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
    @Column(name = "DATE_ENC")
    private Date dateEnc;
}
