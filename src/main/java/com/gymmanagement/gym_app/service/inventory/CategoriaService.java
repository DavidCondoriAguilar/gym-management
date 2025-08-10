package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> findAll();
    Page<Categoria> findAll(Pageable pageable);
    Optional<Categoria> findById(Long id);
    Categoria save(Categoria categoria);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
