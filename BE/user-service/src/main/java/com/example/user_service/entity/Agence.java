package com.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AGENCE")
public class Agence {

    @Id
    @Column(name = "CODE_AGENCE", length = 5, nullable = false)
    private String codeAgence;

    @Column(name = "CODE_CENT", length = 3, nullable = false)
    private String codeCent;

    @Column(name = "CODE_GROUPE", length = 2, nullable = false)
    private String codeGroupe;

    @Column(name = "LIB_AGENCE", length = 30, nullable = false)
    private String libAgence;

    @Column(name = "CODE_IBS", length = 3)
    private String codeIbs;

    @Column(name = "CODE_CAISS_CENT", length = 5, nullable = false)
    private String codeCaissCent;

    @Column(name = "NBRE_GAB")
    private Integer nbreGab;

    @Column(name = "MAT_CHEF_AGENCE", length = 6)
    private String matChefAgence;

    public String getCodeAgence() {
        return codeAgence;
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public String getCodeCent() {
        return codeCent;
    }

    public void setCodeCent(String codeCent) {
        this.codeCent = codeCent;
    }

    public String getCodeGroupe() {
        return codeGroupe;
    }

    public void setCodeGroupe(String codeGroupe) {
        this.codeGroupe = codeGroupe;
    }

    public String getLibAgence() {
        return libAgence;
    }

    public void setLibAgence(String libAgence) {
        this.libAgence = libAgence;
    }

    public String getCodeIbs() {
        return codeIbs;
    }

    public void setCodeIbs(String codeIbs) {
        this.codeIbs = codeIbs;
    }

    public String getCodeCaissCent() {
        return codeCaissCent;
    }

    public void setCodeCaissCent(String codeCaissCent) {
        this.codeCaissCent = codeCaissCent;
    }

    public Integer getNbreGab() {
        return nbreGab;
    }

    public void setNbreGab(Integer nbreGab) {
        this.nbreGab = nbreGab;
    }

    public String getMatChefAgence() {
        return matChefAgence;
    }

    public void setMatChefAgence(String matChefAgence) {
        this.matChefAgence = matChefAgence;
    }
}
