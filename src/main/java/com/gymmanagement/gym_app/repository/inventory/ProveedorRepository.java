package com.gymmanagement.gym_app.repository.inventory;

import com.gymmanagement.gym_app.domain.inventory.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRuc(String ruc);
    boolean existsByRuc(String ruc);
    boolean existsByRazonSocial(String razonSocial);
    List<Proveedor> findByRazonSocialContainingIgnoreCase(String razonSocial);
    List<Proveedor> findByActivoTrue();
}
