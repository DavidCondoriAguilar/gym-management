package com.gymmanagement.gym_app.service.impl.inventory;

import com.gymmanagement.gym_app.domain.inventory.Proveedor;
import com.gymmanagement.gym_app.repository.inventory.ProveedorRepository;
import com.gymmanagement.gym_app.service.inventory.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Proveedor> findAll(Pageable pageable) {
        return proveedorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proveedor> findById(Long id) {
        return proveedorRepository.findById(id);
    }

    @Override
    @Transactional
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRuc(String ruc) {
        return proveedorRepository.existsByRuc(ruc);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRazonSocial(String razonSocial) {
        return proveedorRepository.existsByRazonSocial(razonSocial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> findByRazonSocialContainingIgnoreCase(String razonSocial) {
        return proveedorRepository.findByRazonSocialContainingIgnoreCase(razonSocial);
    }
}
