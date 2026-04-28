package com.example.auth_service.dto;

public record AuthenticatedUserInfo(
        String usrMat,
        String codeAgence,
        String codeProfil,
        String usrName,
        String suspendu,
        String libAgence,
        String libCaisseCent,
        String libGroupe
) {
}
