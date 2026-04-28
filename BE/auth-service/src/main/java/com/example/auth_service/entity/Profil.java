package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "PROFIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Profil implements Serializable {

    @Id
    @Column(name = "CODE_PROFIL", length = 3)
    private String codeProfil;

    @Column(name = "LIB_PROFIL", nullable = false, length = 30)
    private String libProfil;

    @OneToMany(mappedBy = "profil", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Users> users;
}
