package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "GROUPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Groupe implements Serializable {

    @Id
    @Column(name = "CODE_GROUPE", length = 2)
    private String codeGroupe;

    @Column(name = "LIB_GROUPE", nullable = false, length = 40)
    private String libGroupe;

    @Column(name = "MAT_CHEF_GROUPE", length = 6)
    private String matChefGroupe;

    @Column(name = "NAME_CHEF_GROUPE", length = 100)
    private String nameChefGroupe;

    @OneToMany(mappedBy = "groupe", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Agence> agences;
}
