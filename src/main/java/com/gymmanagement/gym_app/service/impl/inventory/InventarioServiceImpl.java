package com.gymmanagement.gym_app.service.impl.inventory;

import com.gymmanagement.gym_app.domain.inventory.Inventario;
import com.gymmanagement.gym_app.domain.inventory.Producto;
import com.gymmanagement.gym_app.domain.enums.TipoMovimientoInventario;
import com.gymmanagement.gym_app.dto.inventory.requests.MovimientoInventarioRequest;
import com.gymmanagement.gym_app.exception.InsufficientStockException;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.repository.inventory.InventarioRepository;
import com.gymmanagement.gym_app.repository.inventory.ProductoRepository;
import com.gymmanagement.gym_app.service.inventory.InventarioService;
import com.gymmanagement.gym_app.service.inventory.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    @Override
    @Transactional
    public Inventario registrarMovimiento(Inventario movimiento) {
        // Validar que el producto existe
        Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        // Actualizar el stock del producto
        if (movimiento.getTipoMovimiento() == TipoMovimientoInventario.SALIDA) {
            if (producto.getStock() < movimiento.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            producto.actualizarStock(-movimiento.getCantidad());
        } else {
            producto.actualizarStock(movimiento.getCantidad());
        }
        
        productoRepository.save(producto);
        return inventarioRepository.save(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> obtenerMovimientosPorProducto(Producto producto) {
        return inventarioRepository.findByProducto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> obtenerMovimientosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return inventarioRepository.findByProductoAndFechas(null, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerStockActual(Producto producto) {
        Integer entradas = inventarioRepository.sumEntradasByProducto(producto.getId());
        Integer salidas = inventarioRepository.sumSalidasByProducto(producto.getId());
        return (entradas != null ? entradas : 0) - (salidas != null ? salidas : 0);
    }

    @Override
    @Transactional
    public void registrarEntrada(Producto producto, int cantidad, String descripcion, Long usuarioId) {
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento(TipoMovimientoInventario.ENTRADA);
        movimiento.setDescripcion(descripcion);
        
        // Aquí se asume que tienes un método para obtener el usuario por ID
        // movimiento.setUsuarioRegistro(usuarioService.findById(usuarioId));
        
        registrarMovimiento(movimiento);
    }

    @Override
    @Transactional
    public void registrarSalida(Producto producto, int cantidad, String descripcion, Long usuarioId, Long ventaId) {
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento(TipoMovimientoInventario.SALIDA);
        movimiento.setDescripcion(descripcion);
        
        // Aquí se asume que tienes un método para obtener el usuario por ID
        // movimiento.setUsuarioRegistro(usuarioService.findById(usuarioId));
        
        if (ventaId != null) {
            // Aquí se asume que tienes una relación con Venta
            // movimiento.setVenta(ventaRepository.findById(ventaId).orElse(null));
        }
        
        registrarMovimiento(movimiento);
    }

    @Override
    @Transactional
    public Inventario registrarMovimiento(MovimientoInventarioRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.getProductoId()));
        
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setDescripcion(request.getDescripcion());
        
        // Aquí se asume que tienes un método para obtener el usuario por ID
        // movimiento.setUsuarioRegistro(usuarioService.findById(request.getUsuarioId()));
        
        if (request.getVentaId() != null) {
            // Aquí se asume que tienes una relación con Venta
            // movimiento.setVenta(ventaRepository.findById(request.getVentaId()).orElse(null));
        }
        
        return registrarMovimiento(movimiento);
    }
}
