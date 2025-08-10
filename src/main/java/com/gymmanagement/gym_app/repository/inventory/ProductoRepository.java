package com.gymmanagement.gym_app.repository.inventory;

import com.gymmanagement.gym_app.domain.inventory.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByProveedorId(Long proveedorId);
    List<Producto> findByActivoTrue();
    
    Page<Producto> findAllByActivoTrue(Pageable pageable);
    
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:termino% OR p.codigo LIKE %:termino%")
    List<Producto> buscarPorTermino(@Param("termino") String termino);
}
