package com.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GROUPE")
public class Groupe {

    @Id
    @Column(name = "CODE_GROUPE", length = 2, nullable = false)
    private String codeGroupe;

    @Column(name = "LIB_GROUPE", length = 40, nullable = false)
    private String libGroupe;

    @Column(name = "MAT_CHEF_GROUPE", length = 6)
    private String matChefGroupe;

    @Column(name = "NAME_CHEF_GROUPE", length = 100)
    private String nameChefGroupe;

    public String getCodeGroupe() {
        return codeGroupe;
    }

    public void setCodeGroupe(String codeGroupe) {
        this.codeGroupe = codeGroupe;
    }

    public String getLibGroupe() {
        return libGroupe;
    }

    public void setLibGroupe(String libGroupe) {
        this.libGroupe = libGroupe;
    }

    public String getMatChefGroupe() {
        return matChefGroupe;
    }

    public void setMatChefGroupe(String matChefGroupe) {
        this.matChefGroupe = matChefGroupe;
    }

    public String getNameChefGroupe() {
        return nameChefGroupe;
    }

    public void setNameChefGroupe(String nameChefGroupe) {
        this.nameChefGroupe = nameChefGroupe;
    }
}
