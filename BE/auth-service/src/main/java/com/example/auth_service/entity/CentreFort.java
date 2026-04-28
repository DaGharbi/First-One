package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "CENTRE_FORT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CentreFort implements Serializable {

    @Id
    @Column(name = "CODE_CENT", length = 3)
    private String codeCent;

    @Column(name = "LIB_CENT", nullable = false, length = 30)
    private String libCent;

    @OneToMany(mappedBy = "centreFort", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Agence> agences;
}
