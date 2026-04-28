package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logs {
    @EmbeddedId
    private LogsId id;

    @Column(name = "ACTION", length = 200)
    private String action;
}
