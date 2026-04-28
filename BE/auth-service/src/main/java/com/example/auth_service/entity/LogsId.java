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
public class LogsId implements Serializable {
    @Column(name = "DATE_LOGS")
    private Date dateLogs;

    @Column(name = "MATUSER", length = 6)
    private String matuser;
}
