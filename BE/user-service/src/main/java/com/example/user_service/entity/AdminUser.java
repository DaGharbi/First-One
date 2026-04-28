package com.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USERS")
public class AdminUser {

    @Id
    @Column(name = "USR_MAT", length = 6, nullable = false)
    private String usrMat;

    @Column(name = "CODE_AGENCE", length = 5, nullable = false)
    private String codeAgence;

    @Column(name = "CODE_PROFIL", length = 3, nullable = false)
    private String codeProfil;

    @Column(name = "USR_NAME", length = 100, nullable = false)
    private String usrName;

    @Column(name = "SUSPENDU", length = 1)
    private String suspendu;

    public String getUsrMat() {
        return usrMat;
    }

    public void setUsrMat(String usrMat) {
        this.usrMat = usrMat;
    }

    public String getCodeAgence() {
        return codeAgence;
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public String getCodeProfil() {
        return codeProfil;
    }

    public void setCodeProfil(String codeProfil) {
        this.codeProfil = codeProfil;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getSuspendu() {
        return suspendu;
    }

    public void setSuspendu(String suspendu) {
        this.suspendu = suspendu;
    }
}
