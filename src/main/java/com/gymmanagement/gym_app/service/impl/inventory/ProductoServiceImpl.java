package com.gymmanagement.gym_app.service.impl.inventory;

import com.gymmanagement.gym_app.domain.inventory.Producto;
import com.gymmanagement.gym_app.dto.inventory.ProductoDTO;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.repository.inventory.ProductoRepository;
import com.gymmanagement.gym_app.service.inventory.ProductoService;
import lombok.RequiredArgsConstructor;
import com.gymmanagement.gym_app.mapper.InventoryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Producto> findAll(Pageable pageable) {
        return productoRepository.findAllByActivoTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::isActivo);
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto update(Producto producto) {
        Producto existingProducto = productoRepository.findById(producto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + producto.getId()));
        
        // Actualizar solo los campos necesarios
        existingProducto.setNombre(producto.getNombre());
        existingProducto.setDescripcion(producto.getDescripcion());
        existingProducto.setPrecioCompra(producto.getPrecioCompra());
        existingProducto.setPrecioVenta(producto.getPrecioVenta());
        existingProducto.setStock(producto.getStock());
        existingProducto.setStockMinimo(producto.getStockMinimo());
        existingProducto.setImagenUrl(producto.getImagenUrl());
        
        return productoRepository.save(existingProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    @Override
    @Transactional
    public Producto actualizarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));
        
        producto.actualizarStock(cantidad);
        return productoRepository.save(producto);
    }

    @Override
    public ProductoDTO convertToDTO(Producto producto) {
        return inventoryMapper.toProductoDTO(producto);
    }

    @Override
    public Producto convertToEntity(ProductoDTO productoDTO) {
        return inventoryMapper.toProducto(productoDTO);
    }
    
    @Override
    public List<ProductoDTO> convertToDTOList(List<Producto> productos) {
        return inventoryMapper.toProductoDTOList(productos);
    }
}
