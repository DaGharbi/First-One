package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "PRESTATAIRE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Prestataire {
    @Id
    @Column(name = "CODE_PREST", nullable = false, length = 2)
    private String codePrest;

    @Column(name = "LIB_PREST", nullable = false, length = 40)
    private String libPrest;

    @Column(name = "CHEMIN_FICHIER_CONVOYEUR", length = 100)
    private String cheminFichierConvoyeur;
}
