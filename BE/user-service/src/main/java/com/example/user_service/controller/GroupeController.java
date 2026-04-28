package com.example.user_service.controller;

import com.example.user_service.entity.Groupe;
import com.example.user_service.repository.AgenceRepository;
import com.example.user_service.repository.GroupeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/groupes")
public class GroupeController {

    private final GroupeRepository groupeRepository;
    private final AgenceRepository agenceRepository;

    public GroupeController(GroupeRepository groupeRepository, AgenceRepository agenceRepository) {
        this.groupeRepository = groupeRepository;
        this.agenceRepository = agenceRepository;
    }

    @GetMapping
    public List<Groupe> listGroupes() {
        return groupeRepository.findAll(Sort.by("codeGroupe"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Groupe createGroupe(@RequestBody Groupe groupe) {
        normalizeGroupe(groupe);
        if (groupe.getCodeGroupe() == null || groupe.getCodeGroupe().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CODE_GROUPE is required");
        }
        if (groupeRepository.existsByNormalizedCodeGroupe(groupe.getCodeGroupe())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Groupe already exists");
        }
        return groupeRepository.save(groupe);
    }

    @PutMapping("/{codeGroupe}")
    public Groupe updateGroupe(@PathVariable String codeGroupe, @RequestBody Groupe payload) {
        Groupe existing = groupeRepository.findByNormalizedCodeGroupe(codeGroupe.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe not found"));

        normalizeGroupe(payload);
        existing.setLibGroupe(payload.getLibGroupe());
        existing.setMatChefGroupe(payload.getMatChefGroupe());
        existing.setNameChefGroupe(payload.getNameChefGroupe());
        return groupeRepository.save(existing);
    }

    @DeleteMapping("/{codeGroupe}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupe(@PathVariable String codeGroupe) {
        String normalizedCodeGroupe = codeGroupe.trim();
        Groupe existing = groupeRepository.findByNormalizedCodeGroupe(normalizedCodeGroupe).orElse(null);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe not found");
        }

        if (agenceRepository.countByNormalizedCodeGroupe(normalizedCodeGroupe) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Delete agences linked to this groupe first");
        }
        groupeRepository.delete(existing);
    }

    private void normalizeGroupe(Groupe groupe) {
        groupe.setCodeGroupe(trim(groupe.getCodeGroupe()));
        groupe.setLibGroupe(trim(groupe.getLibGroupe()));
        groupe.setMatChefGroupe(trim(groupe.getMatChefGroupe()));
        groupe.setNameChefGroupe(trim(groupe.getNameChefGroupe()));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
