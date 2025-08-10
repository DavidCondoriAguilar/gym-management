package com.gymmanagement.gym_app.repository.inventory;

import com.gymmanagement.gym_app.domain.Usuario;
import com.gymmanagement.gym_app.domain.enums.EstadoVenta;
import com.gymmanagement.gym_app.domain.inventory.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByCliente(Usuario cliente);
    List<Venta> findByUsuario(Usuario usuario);
    List<Venta> findByEstado(EstadoVenta estado);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT v FROM Venta v WHERE v.total >= :montoMinimo")
    Page<Venta> findByMontoMinimo(@Param("montoMinimo") Double montoMinimo, Pageable pageable);
    
    @Query("SELECT v FROM Venta v WHERE v.cliente.id = :clienteId")
    List<Venta> findByClienteId(@Param("clienteId") UUID clienteId);
    
    @Query("SELECT v FROM Venta v WHERE v.usuario.id = :usuarioId")
    List<Venta> findByUsuarioId(@Param("usuarioId") UUID usuarioId);
    
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado = 'COMPLETADA' AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Double calcularVentasTotalesPorRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = 'COMPLETADA' AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long contarVentasPorRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT v FROM Venta v JOIN v.detalles d WHERE d.producto.id = :productoId")
    List<Venta> findByProductoId(@Param("productoId") Long productoId);
}
