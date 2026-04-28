package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CAISSE_CENTR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CaisseCentr implements Serializable {

    @Id
    @Column(name = "CODE_CAISSE_CENT", length = 5)
    private String codeCaissCent;

    @Column(name = "LIB_CAISSE_CENT", nullable = false, length = 30)
    private String libCaisseCent;

    @OneToMany(mappedBy = "caisseCentr", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Agence> agences;
}
