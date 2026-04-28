package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservLiqId implements Serializable {
    @Column(name = "CODE_AGENCE", nullable = false, length = 5)
    private String codeAgence;

    @Column(name = "CODE_DEVISE", nullable = false, length = 3)
    private String codeDevise;

    @Temporal(jakarta.persistence.TemporalType.DATE)
    @Column(name = "DATE_RES", nullable = false)
    private Date dateRes;

    @Column(name = "TYPE_RES", nullable = false, length = 20)
    private String typeRes;
}
