package com.example.user_service.repository;

import com.example.user_service.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupeRepository extends JpaRepository<Groupe, String> {
    @Query("select count(g) > 0 from Groupe g where trim(g.codeGroupe) = :codeGroupe")
    boolean existsByNormalizedCodeGroupe(@Param("codeGroupe") String codeGroupe);

    @Query("select g from Groupe g where trim(g.codeGroupe) = :codeGroupe")
    Optional<Groupe> findByNormalizedCodeGroupe(@Param("codeGroupe") String codeGroupe);
}
