package com.example.auth_service.repository;

import com.example.auth_service.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, String> {

    @Query("select a from Agence a where trim(a.codeAgence) = :codeAgence")
    Optional<Agence> findByNormalizedCodeAgence(@Param("codeAgence") String codeAgence);
}
