package com.lifeverse.auth.repository;

import com.lifeverse.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // ✅ Add this line to enable email existence check
    boolean existsByEmail(String email);
}

