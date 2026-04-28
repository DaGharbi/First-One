package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "PARAM_GEN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamGen {
    @Id
    @Column(name = "ID_PARAM", length = 3)
    private String idParam;

    @Column(name = "NOM", nullable = false, length = 100)
    private String nom;

    @Column(name = "VALEUR", nullable = false, length = 50)
    private String valeur;
}
