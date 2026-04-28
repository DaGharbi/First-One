package com.example.auth_service.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LOGS_MAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogsMail {
    @EmbeddedId
    private LogsMailId id;
}
