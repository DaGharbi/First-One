package com.example.user_service.controller;

import com.example.user_service.entity.Agence;
import com.example.user_service.repository.AdminUserRepository;
import com.example.user_service.repository.AgenceRepository;
import com.example.user_service.repository.GroupeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/agences")
public class AgenceController {

    private final AgenceRepository agenceRepository;
    private final GroupeRepository groupeRepository;
    private final AdminUserRepository userRepository;

    public AgenceController(
            AgenceRepository agenceRepository,
            GroupeRepository groupeRepository,
            AdminUserRepository userRepository
    ) {
        this.agenceRepository = agenceRepository;
        this.groupeRepository = groupeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Agence> listAgences() {
        return agenceRepository.findAll(Sort.by("codeAgence"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agence createAgence(@RequestBody Agence agence) {
        normalizeAgence(agence);
        if (agence.getCodeAgence() == null || agence.getCodeAgence().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CODE_AGENCE is required");
        }
        if (agence.getCodeAgence() != null && agenceRepository.existsByNormalizedCodeAgence(agence.getCodeAgence())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agence already exists");
        }
        if (agence.getCodeGroupe() == null || !groupeRepository.existsByNormalizedCodeGroupe(agence.getCodeGroupe())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid CODE_GROUPE is required");
        }
        return agenceRepository.save(agence);
    }

    @PutMapping("/{codeAgence}")
    public Agence updateAgence(@PathVariable String codeAgence, @RequestBody Agence payload) {
        Agence existing = agenceRepository.findByNormalizedCodeAgence(codeAgence.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence not found"));

        normalizeAgence(payload);
        if (payload.getCodeGroupe() != null && !groupeRepository.existsByNormalizedCodeGroupe(payload.getCodeGroupe())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid CODE_GROUPE is required");
        }

        existing.setCodeCent(payload.getCodeCent());
        existing.setCodeGroupe(payload.getCodeGroupe());
        existing.setLibAgence(payload.getLibAgence());
        existing.setCodeIbs(payload.getCodeIbs());
        existing.setCodeCaissCent(payload.getCodeCaissCent());
        existing.setNbreGab(payload.getNbreGab());
        existing.setMatChefAgence(payload.getMatChefAgence());
        return agenceRepository.save(existing);
    }

    @DeleteMapping("/{codeAgence}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgence(@PathVariable String codeAgence) {
        String normalizedCodeAgence = codeAgence.trim();
        Agence existing = agenceRepository.findByNormalizedCodeAgence(normalizedCodeAgence).orElse(null);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence not found");
        }

        if (userRepository.countByNormalizedCodeAgence(normalizedCodeAgence) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Delete users linked to this agence first");
        }
        agenceRepository.delete(existing);
    }

    private void normalizeAgence(Agence agence) {
        agence.setCodeAgence(trim(agence.getCodeAgence()));
        agence.setCodeCent(trim(agence.getCodeCent()));
        agence.setCodeGroupe(trim(agence.getCodeGroupe()));
        agence.setLibAgence(trim(agence.getLibAgence()));
        agence.setCodeIbs(trim(agence.getCodeIbs()));
        agence.setCodeCaissCent(trim(agence.getCodeCaissCent()));
        agence.setMatChefAgence(trim(agence.getMatChefAgence()));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
