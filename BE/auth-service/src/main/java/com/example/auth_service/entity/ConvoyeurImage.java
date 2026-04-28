package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "IMAGE_CP_CONVOYEURS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvoyeurImage {
    @Id
    @Column(name = "CIN", length = 10)
    private String cin;

    /**
     * In legacy Oracle schemas this column is often LONG RAW.
     * Force Hibernate to read/write it as bytes (avoid ResultSet#getBlob).
     */
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "IMAGE_CP")
    private byte[] imageCp;
}
