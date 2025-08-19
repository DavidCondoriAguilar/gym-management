package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.Usuario;
import com.gymmanagement.gym_app.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    List<Usuario> findByRole(Role role);
}
