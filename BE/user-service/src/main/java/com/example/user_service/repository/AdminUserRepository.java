package com.example.user_service.repository;

import com.example.user_service.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminUserRepository extends JpaRepository<AdminUser, String> {
    long countByCodeAgence(String codeAgence);

    @Query("select count(u) from AdminUser u where trim(u.codeAgence) = :codeAgence")
    long countByNormalizedCodeAgence(@Param("codeAgence") String codeAgence);
}
