package com.example.user_service.repository;

import com.example.user_service.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, String> {
    long countByCodeGroupe(String codeGroupe);

    @Query("select count(a) > 0 from Agence a where trim(a.codeAgence) = :codeAgence")
    boolean existsByNormalizedCodeAgence(@Param("codeAgence") String codeAgence);

    @Query("select a from Agence a where trim(a.codeAgence) = :codeAgence")
    Optional<Agence> findByNormalizedCodeAgence(@Param("codeAgence") String codeAgence);

    @Query("select count(a) from Agence a where trim(a.codeGroupe) = :codeGroupe")
    long countByNormalizedCodeGroupe(@Param("codeGroupe") String codeGroupe);
}
