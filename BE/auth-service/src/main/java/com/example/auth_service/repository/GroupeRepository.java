package com.example.auth_service.repository;

import com.example.auth_service.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupeRepository extends JpaRepository<Groupe, String> {

    @Query("select g from Groupe g where trim(g.codeGroupe) = :codeGroupe")
    Optional<Groupe> findByNormalizedCodeGroupe(@Param("codeGroupe") String codeGroupe);
}
