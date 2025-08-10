package com.gymmanagement.gym_app.service.impl.inventory;

import com.gymmanagement.gym_app.domain.inventory.DetalleVenta;
import com.gymmanagement.gym_app.repository.inventory.DetalleVentaRepository;
import com.gymmanagement.gym_app.service.inventory.DetalleVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> findAll() {
        return detalleVentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DetalleVenta> findAll(Pageable pageable) {
        return detalleVentaRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetalleVenta> findById(Long id) {
        return detalleVentaRepository.findById(id);
    }

    @Override
    @Transactional
    public DetalleVenta save(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        detalleVentaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> findByVentaId(Long ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> findByProductoId(Long productoId) {
        return detalleVentaRepository.findByProductoId(productoId);
    }
}
