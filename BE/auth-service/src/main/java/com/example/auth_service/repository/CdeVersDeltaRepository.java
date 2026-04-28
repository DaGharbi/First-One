package com.example.auth_service.repository;

import com.example.auth_service.entity.CdeVersDelta;
import com.example.auth_service.entity.CdeVersDeltaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CdeVersDeltaRepository extends JpaRepository<CdeVersDelta, CdeVersDeltaId> {

    @Query("""
            select c from CdeVersDelta c
            left join fetch c.devise d
            where trim(c.id.codeAgence) in (
                select trim(a.codeAgence) from Agence a
                where trim(a.codeCaissCent) = :codeCaissCent
            )
            order by c.id.datePass desc, c.id.codeAgence asc
            """)
    List<CdeVersDelta> findByCaisseCentrale(@Param("codeCaissCent") String codeCaissCent);

    @Query("""
            select c from CdeVersDelta c
            left join fetch c.agence a
            left join fetch c.devise d
            where trim(c.id.codeAgence) = :codeAgence
            order by c.id.datePass desc, c.id.codeDevise asc
            """)
    List<CdeVersDelta> findByCodeAgence(@Param("codeAgence") String codeAgence);
}
