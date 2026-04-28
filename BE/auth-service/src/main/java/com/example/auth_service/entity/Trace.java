package com.example.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "TRACE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trace {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_trace")
    @SequenceGenerator(name = "seq_trace", sequenceName = "SEQ_TRACE", allocationSize = 1)
    @Column(name = "NUMSEQ")
    private Long numseq;

    @Column(name = "MATUSER", nullable = false, length = 6)
    private String matuser;

    @Column(name = "CODUG", length = 5)
    private String codug;

    @Column(name = "OBSERVATION", length = 1000)
    private String observation;

    @Column(name = "TYPOPER", length = 1)
    private String typoper;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATTRACE", nullable = false)
    private Date dattrace;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATESESSION")
    private Date datesession;
}
