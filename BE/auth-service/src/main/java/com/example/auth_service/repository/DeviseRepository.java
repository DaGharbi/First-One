package com.example.auth_service.repository;

import com.example.auth_service.entity.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviseRepository extends JpaRepository<Devise, String> {
    List<Devise> findAllByOrderByCodeDeviseAsc();

    @Query("select d from Devise d where trim(d.codeDevise) = :codeDevise")
    Optional<Devise> findByNormalizedCodeDevise(@Param("codeDevise") String codeDevise);
}
