package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APP_PROFILE_PERMISSION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AppProfilePermissionId.class)
public class AppProfilePermission {

    @Id
    @Column(name = "CODE_PROFIL")
    private String codeProfil;

    @Id
    @Column(name = "CODE_RESOURCE")
    private String codeResource;

    @Id
    @Column(name = "CODE_ACTION")
    private String codeAction;

    @Column(name = "ENABLED")
    private String enabled; // Y/N
}

