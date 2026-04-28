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
public class OperationCaisseId implements Serializable {
    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;

    @Column(name = "DATE_OP")
    private Date dateOp;

    @Column(name = "NUM_PV", precision = 10, scale = 0)
    private Long numPv;
}
