package com.example.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "DEVISE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Devise {
    @Id
    @Column(name = "CODE_DEVISE", length = 3)
    private String codeDevise;

    @Column(name = "LIB_DEVISE", length = 30)
    private String libDevise;
}
