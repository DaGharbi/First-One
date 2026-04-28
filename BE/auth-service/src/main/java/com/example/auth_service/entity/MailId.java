package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailId implements Serializable {
    @Column(name = "DATE_MAIL")
    private Date dateMail;

    @Column(name = "CODE_AGENCE", length = 5)
    private String codeAgence;
}
