package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<Proveedor> findAll();
    Page<Proveedor> findAll(Pageable pageable);
    Optional<Proveedor> findById(Long id);
    Proveedor save(Proveedor proveedor);
    void deleteById(Long id);
    boolean existsByRuc(String ruc);
    boolean existsByRazonSocial(String razonSocial);
    List<Proveedor> findByRazonSocialContainingIgnoreCase(String razonSocial);
}
