package com.example.auth_service.service;

import com.example.auth_service.dto.CdeVersDeltaInfo;
import com.example.auth_service.dto.CdeVersDeltaRequest;
import com.example.auth_service.entity.Agence;
import com.example.auth_service.entity.CdeVersDelta;
import com.example.auth_service.entity.CdeVersDeltaId;
import com.example.auth_service.entity.Devise;
import com.example.auth_service.repository.AgenceRepository;
import com.example.auth_service.repository.CdeVersDeltaRepository;
import com.example.auth_service.repository.DeviseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Service
public class CdeVersDeltaService {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CdeVersDeltaRepository cdeVersDeltaRepository;
    private final AgenceRepository agenceRepository;
    private final DeviseRepository deviseRepository;

    public CdeVersDeltaService(
            CdeVersDeltaRepository cdeVersDeltaRepository,
            AgenceRepository agenceRepository,
            DeviseRepository deviseRepository
    ) {
        this.cdeVersDeltaRepository = cdeVersDeltaRepository;
        this.agenceRepository = agenceRepository;
        this.deviseRepository = deviseRepository;
    }

    public ResponseEntity<?> create(CdeVersDeltaRequest request) {
        String codeAgence = normalize(request.codeAgence());
        if (codeAgence == null || codeAgence.isEmpty()) {
            return ResponseEntity.badRequest().body("Code agence obligatoire");
        }

        Agence agence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        if (agence == null) {
            return ResponseEntity.badRequest().body("Agence introuvable");
        }

        String codeDevise = normalize(request.codeDevise());
        if (codeDevise == null || codeDevise.isEmpty()) {
            return ResponseEntity.badRequest().body("Devise obligatoire");
        }

        Devise devise = deviseRepository.findByNormalizedCodeDevise(codeDevise).orElse(null);
        if (devise == null) {
            return ResponseEntity.badRequest().body("Devise introuvable");
        }

        String naturePass = normalize(request.naturePass());
        if (naturePass == null || naturePass.isEmpty()) {
            return ResponseEntity.badRequest().body("Nature obligatoire");
        }

        LocalDate datePass = parseDisplayDate(request.datePass());
        if (datePass == null) {
            return ResponseEntity.badRequest().body("Date invalide. Format attendu: dd-mm-yyyy");
        }

        Short cdeVers = request.cdeVers();
        if (cdeVers == null || (cdeVers != 0 && cdeVers != 1 && cdeVers != 2)) {
            return ResponseEntity.badRequest().body("Operation invalide");
        }

        CdeVersDelta command = CdeVersDelta.builder()
                .id(CdeVersDeltaId.builder()
                        .codeAgence(agence.getCodeAgence())
                        .codeDevise(devise.getCodeDevise())
                        .datePass(toDate(datePass))
                        .naturePass(naturePass)
                        .build())
                .agence(agence)
                .devise(devise)
                .montant(request.montant() == null ? BigDecimal.ZERO : request.montant())
                .cdeVers(cdeVers)
                .descriptionClient(normalize(request.descriptionClient()))
                .isBillets(request.isBillets() != null && request.isBillets())
                .build();

        return ResponseEntity.ok(toInfo(cdeVersDeltaRepository.save(command)));
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

        List<CdeVersDeltaInfo> commands = cdeVersDeltaRepository.findByCaisseCentrale(codeCaissCent)
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

        List<CdeVersDeltaInfo> commands = cdeVersDeltaRepository.findByCodeAgence(codeAgence)
                .stream()
                .map(this::toInfo)
                .toList();
        return ResponseEntity.ok(commands);
    }

    private CdeVersDeltaInfo toInfo(CdeVersDelta command) {
        String codeAgence = normalize(command.getId().getCodeAgence());
        String codeDevise = normalize(command.getId().getCodeDevise());
        Agence agence = agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        Devise devise = deviseRepository.findByNormalizedCodeDevise(codeDevise).orElse(null);

        return new CdeVersDeltaInfo(
                codeAgence,
                agence == null ? null : normalize(agence.getLibAgence()),
                codeDevise,
                devise == null ? null : normalize(devise.getLibDevise()),
                formatDisplayDate(command.getId().getDatePass()),
                normalize(command.getId().getNaturePass()),
                normalize(command.getDescriptionClient()),
                command.getIsBillets(),
                command.getMontant(),
                command.getCdeVers()
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

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
