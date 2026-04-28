package com.example.auth_service.repository;

import com.example.auth_service.entity.CaisseCentr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CaisseCentrRepository extends JpaRepository<CaisseCentr, String> {

    @Query("select c from CaisseCentr c where trim(c.codeCaissCent) = :codeCaissCent")
    Optional<CaisseCentr> findByNormalizedCodeCaissCent(@Param("codeCaissCent") String codeCaissCent);
}
