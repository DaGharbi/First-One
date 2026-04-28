package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogsMailId implements Serializable {
    @Column(name = "ID", nullable = false, precision = 22, scale = 0)
    private BigDecimal id;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_GEN", nullable = false)
    private Date dateGen;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_ENV")
    private Date dateEnv;

    @Column(name = "STATUS", length = 1)
    private String status;
}
