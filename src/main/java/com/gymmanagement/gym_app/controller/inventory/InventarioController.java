package com.gymmanagement.gym_app.controller.inventory;

import com.gymmanagement.gym_app.domain.inventory.Inventario;
import com.gymmanagement.gym_app.domain.inventory.Producto;
import com.gymmanagement.gym_app.dto.inventory.requests.MovimientoInventarioRequest;
import com.gymmanagement.gym_app.service.inventory.InventarioService;
import com.gymmanagement.gym_app.service.inventory.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventario/movimientos")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<Inventario> registrarMovimiento(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        return new ResponseEntity<>(
                inventarioService.registrarMovimiento(request), 
                HttpStatus.CREATED
        );
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Inventario>> obtenerMovimientosPorProducto(
            @PathVariable Long productoId) {
        Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return ResponseEntity.ok(inventarioService.obtenerMovimientosPorProducto(producto));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<Inventario>> obtenerMovimientosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(
                inventarioService.obtenerMovimientosPorRangoFechas(fechaInicio, fechaFin)
        );
    }

    @GetMapping("/stock-actual/{productoId}")
    public ResponseEntity<Integer> obtenerStockActual(@PathVariable Long productoId) {
        Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return ResponseEntity.ok(inventarioService.obtenerStockActual(producto));
    }
}
