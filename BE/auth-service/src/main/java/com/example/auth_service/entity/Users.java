package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Users implements Serializable {

    @Id
    @Column(name = "USR_MAT", length = 6)
    private String usrMat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CODE_AGENCE", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Agence agence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CODE_PROFIL", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profil profil;

    @Column(name = "USR_NAME", nullable = false, length = 100)
    private String usrName;

    @Column(name = "SUSPENDU", length = 1)
    private String suspendu;

    // We can add other relationships as needed (e.g., reservLiqs)
    // For now, focusing on core structure
}
