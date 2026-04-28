package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APP_MENU")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppMenu {

    @Id
    @Column(name = "ID_MENU")
    private Long idMenu;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "MENU_TYPE")
    private String menuType; // GROUP | LINK

    @Column(name = "LABEL")
    private String label;

    @Column(name = "ROUTE")
    private String route;

    @Column(name = "ORDER_INDEX")
    private Long orderIndex;

    @Column(name = "RESOURCE_CODE")
    private String resourceCode;

    @Column(name = "ENABLED")
    private String enabled; // Y/N
}

