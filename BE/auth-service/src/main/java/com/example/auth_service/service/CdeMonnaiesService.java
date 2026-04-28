package com.example.auth_service.service;

import com.example.auth_service.dto.CdeMonnaiesInfo;
import com.example.auth_service.dto.CdeMonnaiesRequest;
import com.example.auth_service.entity.Agence;
import com.example.auth_service.entity.CdeMonnaies;
import com.example.auth_service.entity.CdeMonnaiesId;
import com.example.auth_service.entity.Devise;
import com.example.auth_service.repository.AgenceRepository;
import com.example.auth_service.repository.CdeMonnaiesRepository;
import com.example.auth_service.repository.DeviseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Service
public class CdeMonnaiesService {

    private static final String TND_CODE_DEVISE = "TND";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CdeMonnaiesRepository cdeMonnaiesRepository;
    private final AgenceRepository agenceRepository;
    private final DeviseRepository deviseRepository;

    public CdeMonnaiesService(
            CdeMonnaiesRepository cdeMonnaiesRepository,
            AgenceRepository agenceRepository,
            DeviseRepository deviseRepository
    ) {
        this.cdeMonnaiesRepository = cdeMonnaiesRepository;
        this.agenceRepository = agenceRepository;
        this.deviseRepository = deviseRepository;
    }

    public ResponseEntity<?> create(CdeMonnaiesRequest request) {
        String codeAgence = normalize(request.codeAgence());
        if (codeAgence == null || codeAgence.isEmpty()) {
            return ResponseEntity.badRequest().body("Code agence obligatoire");
        }

        Agence agence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        if (agence == null) {
            return ResponseEntity.badRequest().body("Agence introuvable");
        }

        Devise devise = deviseRepository.findByNormalizedCodeDevise(TND_CODE_DEVISE).orElse(null);
        if (devise == null) {
            return ResponseEntity.badRequest().body("Devise TND introuvable");
        }

        LocalDate commandDate = parseDisplayDate(request.dateCdeMonnaies());
        if (commandDate == null) {
            return ResponseEntity.badRequest().body("Date invalide. Format attendu: dd-mm-yyyy");
        }

        CdeMonnaies command = CdeMonnaies.builder()
                .id(CdeMonnaiesId.builder()
                        .codeAgence(agence.getCodeAgence())
                        .codeDevise(devise.getCodeDevise())
                        .dateCdeMonnaies(toDate(commandDate))
                        .build())
                .agence(agence)
                .devise(devise)
                .p100M(nonNull(request.p100M()))
                .p10M(nonNull(request.p10M()))
                .p1Dn(nonNull(request.p1Dn()))
                .p20M(nonNull(request.p20M()))
                .p500M(nonNull(request.p500M()))
                .p50M(nonNull(request.p50M()))
                .p5M(nonNull(request.p5M()))
                .build();

        return ResponseEntity.ok(toInfo(cdeMonnaiesRepository.save(command)));
    }

    public ResponseEntity<?> listForSameCaisseCentrale(String connectedCodeAgence) {
        String codeAgence = normalize(connectedCodeAgence);
        if (codeAgence == null || codeAgence.isEmpty()) {
            return ResponseEntity.badRequest().body("Code agence obligatoire");
        }

        Agence connectedAgence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        if (connectedAgence == null) {
            return ResponseEntity.badRequest().body("Agence introuvable");
        }

        String codeCaissCent = normalize(connectedAgence.getCodeCaissCent());
        if (codeCaissCent == null || codeCaissCent.isEmpty()) {
            return ResponseEntity.badRequest().body("Caisse centrale introuvable");
        }

        List<CdeMonnaiesInfo> commands = cdeMonnaiesRepository.findByCaisseCentrale(codeCaissCent)
                .stream()
                .map(this::toInfo)
                .toList();
        return ResponseEntity.ok(commands);
    }

    public ResponseEntity<?> listForAgency(String connectedCodeAgence) {
        String codeAgence = normalize(connectedCodeAgence);
        if (codeAgence == null || codeAgence.isEmpty()) {
            return ResponseEntity.badRequest().body("Code agence obligatoire");
        }

        Agence connectedAgence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        if (connectedAgence == null) {
            return ResponseEntity.badRequest().body("Agence introuvable");
        }

        List<CdeMonnaiesInfo> commands = cdeMonnaiesRepository.findByCodeAgence(codeAgence)
                .stream()
                .map(this::toInfo)
                .toList();
        return ResponseEntity.ok(commands);
    }

    private CdeMonnaiesInfo toInfo(CdeMonnaies command) {
        String codeAgence = normalize(command.getId().getCodeAgence());
        String codeDevise = normalize(command.getId().getCodeDevise());
        Agence agence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        Devise devise = deviseRepository.findByNormalizedCodeDevise(codeDevise).orElse(null);

        return new CdeMonnaiesInfo(
                codeAgence,
                agence == null ? null : normalize(agence.getLibAgence()),
                codeDevise,
                devise == null ? null : normalize(devise.getLibDevise()),
                formatDisplayDate(command.getId().getDateCdeMonnaies()),
                command.getP100M(),
                command.getP10M(),
                command.getP1Dn(),
                command.getP20M(),
                command.getP500M(),
                command.getP50M(),
                command.getP5M()
        );
    }

    private LocalDate parseDisplayDate(String value) {
        String normalized = normalize(value);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(normalized, DISPLAY_DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String formatDisplayDate(Date value) {
        if (value == null) {
            return null;
        }

        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate().format(DISPLAY_DATE_FORMAT);
        }

        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DISPLAY_DATE_FORMAT);
    }

    private Date toDate(LocalDate value) {
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Long nonNull(Long value) {
        return value == null ? 0L : value;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
