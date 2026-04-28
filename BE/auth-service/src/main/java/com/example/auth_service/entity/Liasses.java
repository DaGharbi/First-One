package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LIASSES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liasses {
    @EmbeddedId
    private LiassesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_DEVISE", insertable = false, updatable = false)
    private Devise devise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_AGENCE", insertable = false, updatable = false)
    private Agence agence;

    @Column(name = "NBRE_LIASSE", precision = 10, scale = 0)
    private Long nbreLiasse;
}
