package com.example.auth_service.repository;

import com.example.auth_service.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, String> {

    @Query("select u from AdminUser u where trim(u.usrMat) = :usrMat")
    Optional<AdminUser> findByNormalizedUsrMat(@Param("usrMat") String usrMat);
}
