package com.example.auth_service.entity;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppProfilePermissionId implements Serializable {
    private String codeProfil;
    private String codeResource;
    private String codeAction;
}

