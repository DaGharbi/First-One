package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mail {
    @EmbeddedId
    private MailId id;

    @Column(name = "OBJET", length = 100)
    private String objet;

    @Column(name = "CORPS", length = 4000)
    private String corps;
}
