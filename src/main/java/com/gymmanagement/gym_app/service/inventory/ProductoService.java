package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.Producto;
import com.gymmanagement.gym_app.dto.inventory.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAll();
    Page<Producto> findAll(Pageable pageable);
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    void deleteById(Long id);
    Producto update(Producto producto);
    List<Producto> findByNombreContaining(String nombre);
    List<Producto> findProductosConStockBajo();
    Producto actualizarStock(Long productoId, int cantidad);
    ProductoDTO convertToDTO(Producto producto);
    Producto convertToEntity(ProductoDTO productoDTO);
    List<ProductoDTO> convertToDTOList(List<Producto> productos);
}
