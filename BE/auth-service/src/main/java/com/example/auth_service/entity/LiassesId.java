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
public class LiassesId implements Serializable {
    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;

    @Column(name = "CODE_DEVISE", length = 3)
    private String codeDevise;

    @Column(name = "TYPE_LIASSE", length = 3)
    private String typeLiasse;
}
