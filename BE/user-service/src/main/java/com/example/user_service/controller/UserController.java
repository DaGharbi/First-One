package com.example.user_service.controller;

import com.example.user_service.entity.AdminUser;
import com.example.user_service.repository.AdminUserRepository;
import com.example.user_service.repository.AgenceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController {

    private final AdminUserRepository userRepository;
    private final AgenceRepository agenceRepository;

    public UserController(AdminUserRepository userRepository, AgenceRepository agenceRepository) {
        this.userRepository = userRepository;
        this.agenceRepository = agenceRepository;
    }

    @GetMapping
    public List<AdminUser> listUsers() {
        return userRepository.findAll(Sort.by("usrMat"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUser createUser(@RequestBody AdminUser user) {
        normalizeUser(user);
        String usrMat = user.getUsrMat();
        if (usrMat == null || usrMat.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USR_MAT is required");
        }
        String codeAgence = user.getCodeAgence();
        if (userRepository.existsById(usrMat)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        if (codeAgence == null || !agenceRepository.existsByNormalizedCodeAgence(codeAgence)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid CODE_AGENCE is required");
        }
        if (user.getSuspendu() == null || user.getSuspendu().isBlank()) {
            user.setSuspendu("N");
        }
        return userRepository.save(user);
    }

    @PutMapping("/{usrMat}")
    public AdminUser updateUser(@PathVariable String usrMat, @RequestBody AdminUser payload) {
        String normalizedUsrMat = requirePathVariable(usrMat, "usrMat").trim();
        AdminUser existing = userRepository.findById(normalizedUsrMat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        normalizeUser(payload);
        String codeAgence = payload.getCodeAgence();
        if (codeAgence != null && !agenceRepository.existsByNormalizedCodeAgence(codeAgence)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid CODE_AGENCE is required");
        }

        existing.setCodeAgence(payload.getCodeAgence());
        existing.setCodeProfil(payload.getCodeProfil());
        existing.setUsrName(payload.getUsrName());
        existing.setSuspendu(payload.getSuspendu() == null || payload.getSuspendu().isBlank() ? "N" : payload.getSuspendu());
        return userRepository.save(existing);
    }

    @DeleteMapping("/{usrMat}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String usrMat) {
        String normalizedUsrMat = requirePathVariable(usrMat, "usrMat").trim();
        if (!userRepository.existsById(normalizedUsrMat)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(normalizedUsrMat);
    }

    private void normalizeUser(AdminUser user) {
        user.setUsrMat(trim(user.getUsrMat()));
        user.setCodeAgence(trim(user.getCodeAgence()));
        user.setCodeProfil(trim(user.getCodeProfil()));
        user.setUsrName(trim(user.getUsrName()));
        user.setSuspendu(trim(user.getSuspendu()));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String requirePathVariable(String value, String fieldName) {
        if (value == null) {
            throw new NullPointerException(fieldName + " path variable is required");
        }
        return value;
    }
}
