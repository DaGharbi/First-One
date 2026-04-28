package com.example.auth_service.service;

import com.example.auth_service.dto.AuthenticatedUserInfo;
import com.example.auth_service.dto.LoginResponse;
import com.example.auth_service.entity.AdminUser;
import com.example.auth_service.entity.Agence;
import com.example.auth_service.entity.CaisseCentr;
import com.example.auth_service.entity.Groupe;
import com.example.auth_service.repository.AdminUserRepository;
import com.example.auth_service.repository.AgenceRepository;
import com.example.auth_service.repository.CaisseCentrRepository;
import com.example.auth_service.repository.GroupeRepository;
import com.example.common.dto.AuthRequest;
import com.example.common.entity.User;
import com.example.common.repository.UserRepository;
import com.example.common.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final AgenceRepository agenceRepository;
    private final CaisseCentrRepository caisseCentrRepository;
    private final GroupeRepository groupeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            AdminUserRepository adminUserRepository,
            AgenceRepository agenceRepository,
            CaisseCentrRepository caisseCentrRepository,
            GroupeRepository groupeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.adminUserRepository = adminUserRepository;
        this.agenceRepository = agenceRepository;
        this.caisseCentrRepository = caisseCentrRepository;
        this.groupeRepository = groupeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> register(AuthRequest request) {
        if (userRepository.findByEmail(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setId(userRepository.findNextId());
        user.setEmail(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }

    public ResponseEntity<?> login(AuthRequest request) {
        String username = request.getUsername() == null ? null : request.getUsername().trim();
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("User not existing");
        }

        User user = userOptional.get();
        String storedPassword = user.getPassword();
        boolean passwordMatches = storedPassword != null && storedPassword.equals(request.getPassword());

        if (!passwordMatches && storedPassword != null) {
            try {
                passwordMatches = passwordEncoder.matches(request.getPassword(), storedPassword);
            } catch (IllegalArgumentException ex) {
                passwordMatches = false;
            }
        }

        if (!passwordMatches) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        AdminUser adminUser = findAdminUser(username);
        if (adminUser == null) {
            return ResponseEntity.status(403).body("User profile not found");
        }

        String authRole = resolveAppRoleFromCodeProfile(adminUser.getCodeProfil());
        if (authRole == null) {
            return ResponseEntity.status(403).body("Profile not allowed");
        }

        String token = jwtService.generateToken(username);
        AuthenticatedUserInfo authenticatedUser = buildAuthenticatedUserInfo(adminUser, authRole, username);

        return ResponseEntity.ok(new LoginResponse(token, authenticatedUser));
    }

    public ResponseEntity<List<String>> listUsers() {
        List<String> usernames = userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .toList();
        return ResponseEntity.ok(usernames);
    }

    private AuthenticatedUserInfo buildAuthenticatedUserInfo(AdminUser adminUser, String authRole, String username) {
        if (adminUser == null && isAdminLikeRole(authRole)) {
            return new AuthenticatedUserInfo(
                    normalize(username),
                    null,
                    null,
                    "Administrateur",
                    null,
                    null,
                    null,
                    null
            );
        }

        if (adminUser == null) {
            return new AuthenticatedUserInfo(
                    normalize(username),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        String normalizedCodeAgence = normalize(adminUser.getCodeAgence());
        Agence agence = findAgence(normalizedCodeAgence);
        String normalizedCodeCaissCent = agence == null ? null : normalize(agence.getCodeCaissCent());
        CaisseCentr caisseCentr = findCaisseCentr(normalizedCodeCaissCent);
        String normalizedCodeGroupe = agence == null ? null : normalize(agence.getCodeGroupe());
        Groupe groupe = findGroupe(normalizedCodeGroupe);

        return new AuthenticatedUserInfo(
                normalize(adminUser.getUsrMat()),
                normalizedCodeAgence,
                normalize(adminUser.getCodeProfil()),
                normalize(adminUser.getUsrName()) == null && isAdminLikeRole(authRole) ? "Administrateur" : normalize(adminUser.getUsrName()),
                normalize(adminUser.getSuspendu()),
                agence == null ? null : normalize(agence.getLibAgence()),
                caisseCentr == null ? null : normalize(caisseCentr.getLibCaisseCent()),
                groupe == null ? null : normalize(groupe.getLibGroupe())
        );
    }

    private String resolveAppRoleFromCodeProfile(String codeProfile) {
        String normalizedCodeProfile = normalize(codeProfile);
        if (normalizedCodeProfile == null || normalizedCodeProfile.isEmpty()) {
            return null;
        }

        return switch (normalizedCodeProfile.toUpperCase()) {
            case "ADM" -> "ADMIN";
            case "SEC" -> "SECURITY";
            case "RAG" -> "AGENT";
            case "RCC" -> "CC";
            default -> null;
        };
    }

    private Agence findAgence(String codeAgence) {
        if (codeAgence == null) {
            return null;
        }

        try {
            return agenceRepository.findByNormalizedCodeAgence(codeAgence).orElse(null);
        } catch (RuntimeException ex) {
            log.warn("Unable to load agence for code {}", codeAgence, ex);
            return null;
        }
    }

    private CaisseCentr findCaisseCentr(String codeCaissCent) {
        if (codeCaissCent == null) {
            return null;
        }

        try {
            return caisseCentrRepository.findByNormalizedCodeCaissCent(codeCaissCent).orElse(null);
        } catch (RuntimeException ex) {
            log.warn("Unable to load caisse centrale for code {}", codeCaissCent, ex);
            return null;
        }
    }

    private Groupe findGroupe(String codeGroupe) {
        if (codeGroupe == null) {
            return null;
        }

        try {
            return groupeRepository.findByNormalizedCodeGroupe(codeGroupe).orElse(null);
        } catch (RuntimeException ex) {
            log.warn("Unable to load groupe for code {}", codeGroupe, ex);
            return null;
        }
    }

    private AdminUser findAdminUser(String usrMat) {
        if (usrMat == null) {
            return null;
        }

        try {
            return adminUserRepository.findByNormalizedUsrMat(usrMat).orElse(null);
        } catch (RuntimeException ex) {
            log.warn("Unable to load user details for usrMat {}", usrMat, ex);
            return null;
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isAdminLikeRole(String authRole) {
        return "ADMIN".equals(authRole) || "CC".equals(authRole) || "SECURITY".equals(authRole);
    }
}
