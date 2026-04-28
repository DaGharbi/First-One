package com.example.auth_service.repository;

import com.example.auth_service.entity.CdeMonnaies;
import com.example.auth_service.entity.CdeMonnaiesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CdeMonnaiesRepository extends JpaRepository<CdeMonnaies, CdeMonnaiesId> {

    @Query("""
            select c from CdeMonnaies c
            left join fetch c.devise d
            where trim(c.id.codeAgence) in (
                select trim(a.codeAgence) from Agence a
                where trim(a.codeCaissCent) = :codeCaissCent
            )
            order by c.id.dateCdeMonnaies desc, c.id.codeAgence asc
            """)
    List<CdeMonnaies> findByCaisseCentrale(@Param("codeCaissCent") String codeCaissCent);

    @Query("""
            select c from CdeMonnaies c
            left join fetch c.agence a
            left join fetch c.devise d
            where trim(c.id.codeAgence) = :codeAgence
            order by c.id.dateCdeMonnaies desc, c.id.codeDevise asc
            """)
    List<CdeMonnaies> findByCodeAgence(@Param("codeAgence") String codeAgence);
}
