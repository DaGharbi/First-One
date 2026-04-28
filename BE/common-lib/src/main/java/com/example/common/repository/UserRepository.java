package com.example.common.repository;

import com.example.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select coalesce(max(u.id), 0) + 1 from User u")
    Long findNextId();
}
