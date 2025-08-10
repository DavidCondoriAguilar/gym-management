package com.gymmanagement.gym_app.repository.inventory;

import com.gymmanagement.gym_app.domain.inventory.Inventario;
import com.gymmanagement.gym_app.domain.inventory.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByProducto(Producto producto);
    
    @Query("SELECT i FROM Inventario i WHERE i.producto.id = :productoId AND i.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin")
    List<Inventario> findByProductoAndFechas(
            @Param("productoId") Long productoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM Inventario i WHERE i.producto.id = :productoId AND i.tipoMovimiento = 'ENTRADA'")
    Integer sumEntradasByProducto(@Param("productoId") Long productoId);
    
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM Inventario i WHERE i.producto.id = :productoId AND i.tipoMovimiento = 'SALIDA'")
    Integer sumSalidasByProducto(@Param("productoId") Long productoId);
}
