package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "AGENCE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Agence implements Serializable, Comparable<Agence> {

    @Id
    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_GROUPE", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Groupe groupe;

    @Column(name = "CODE_GROUPE", length = 2, insertable = false, updatable = false)
    private String codeGroupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAT_CHEF_AGENCE")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Users chefAgence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CENT", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CentreFort centreFort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE_CAISS_CENT", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CaisseCentr caisseCentr;

    @Column(name = "CODE_CAISS_CENT", length = 5, insertable = false, updatable = false)
    private String codeCaissCent;

    @Column(name = "LIB_AGENCE", nullable = false, length = 30)
    private String libAgence;

    @Column(name = "CODE_IBS", length = 3)
    private String codeIbs;

    @Column(name = "NBRE_GAB", nullable = false)
    private Short nbreGab;

    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Users> users;

    @Override
    public int compareTo(Agence other) {
        return this.codeAgence.compareTo(other.codeAgence);
    }
}
