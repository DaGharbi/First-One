package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ADRESSE_MAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresseMail {
    @Id
    @Column(name = "USR_MAT", length = 4)
    private String usrMat;

    @Column(name = "ADRESSE_MAIL", length = 100, nullable = false)
    private String adresseMail;
}
